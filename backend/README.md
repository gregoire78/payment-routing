# Backend Routage des Paiements

## Stack Technologique

- Java 21+
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring JMS
- Client IBM MQ Jakarta
- Maven + JaCoCo
- JUnit 5 + Mockito

## Architecture

### Approche : Architecture Hexagonale (Ports et Adaptateurs)

Cette application suit une **architecture hexagonale** (aussi appelée ports et adaptateurs) qui organise le code en trois couches concentriques :

1. **Cœur du Domaine** (au centre) : contient la logique métier pure, indépendante de tout framework
2. **Couche Application** (anneau intermédiaire) : définit les **ports** (interfaces d'abstraction) et les cas d'usage
3. **Infrastructure** (anneau externe) : implémente les **adaptateurs** qui connectent le domaine aux systèmes externes (MQ, BDD, API, etc.)

**Avantages** :
- ✅ Testabilité : le domaine ne dépend d'aucun framework
- ✅ Flexibilité : remplacer MQ par Kafka, JDBC par JPA, sans toucher au domaine
- ✅ Maintenabilité : les responsabilités sont clairement séparées
- ✅ Indépendance de la technologie : l'implémentation technique est isolée

### Ports et Adaptateurs

Les **ports** sont des interfaces définies dans la couche application.
On distingue :

- **Ports entrants (input ports)** : contrats exposés par les cas d'usage
- **Ports sortants (output ports)** : contrats requis par les cas d'usage vers l'infrastructure

Ports entrants :

| Port | Responsabilité | Implémentation |
|------|----------------|----------------|
| `IngestMessagePort` | Point d'entrée applicatif pour l'ingestion | `IngestMessageUseCase` |
| `ListMessagesPort` | Point d'entrée applicatif pour la liste | `ListMessagesUseCase` |
| `GetMessagePort` | Point d'entrée applicatif pour la consultation | `GetMessageUseCase` |
| `PublishMessagePort` | Point d'entrée applicatif pour la publication | `PublishMessageUseCase` |

Ports sortants :

| Port | Responsabilité | Adaptateurs |
|------|----------------|-------------|
| `PaymentMessageQueryPort` | Requêtes en lecture | `JpaPaymentMessageQueryAdapter` |
| `PaymentMessageStorePort` | Persistance en écriture | `JpaPaymentMessageStoreAdapter` |
| `PaymentMessagePublisherPort` | Publication MQ | `MqPaymentMessagePublisherAdapter` |
| `DomainEventPublisherPort` | Publication d'événements domaine | `SpringDomainEventPublisherAdapter` |

Les **adaptateurs** sont les implémentations techniques dans l'infrastructure :
- Adaptateur JPA pour la persistance → utilise Spring Data JPA + Hibernate
- Adaptateur MQ pour la publication → utilise IBM MQ Jakarta client
- Adaptateur Spring pour les événements domaine → utilise Spring Events

**Bénéfice** : Si demain on change MQ pour Kafka, on crée un nouvel adaptateur sans modifier le cœur métier.

### Diagramme Architecture

```mermaid
flowchart TB
  %% =========================
  %% Couche 1 : Infrastructure
  %% =========================
  subgraph INFRA[Couche Infrastructure]
    direction TB

    subgraph INBOUND[Adaptateurs Entrants]
      REST[API REST Controller]
      MQIN[MQ Listener]
    end

    subgraph OUTBOUND[Adaptateurs Sortants]
      JPA[Adapter Persistance JPA]
      MQOUT[Adapter Publication MQ]
      EV[Adapter Publication Evenements]
    end

    DB[(PostgreSQL)]
    MQ[(IBM MQ)]
  end

  %% =========================
  %% Couche 2 : Application
  %% =========================
  subgraph APP[Couche Application]
    direction TB

    subgraph INPUT_PORTS[Ports Entrants]
      P1[ListMessagesPort]
      P2[GetMessagePort]
      P3[PublishMessagePort]
      P4[IngestMessagePort]
    end

    subgraph USECASES[Cas d usage]
      U1[ListMessagesUseCase]
      U2[GetMessageUseCase]
      U3[PublishMessageUseCase]
      U4[IngestMessageUseCase]
    end

    subgraph OUTPUT_PORTS[Ports Sortants]
      O1[PaymentMessageQueryPort]
      O2[PaymentMessageStorePort]
      O3[PaymentMessagePublisherPort]
      O4[DomainEventPublisherPort]
    end
  end

  %% =========================
  %% Couche 3 : Domaine
  %% =========================
  subgraph DOMAIN[Couche Domaine]
    direction TB
    AGG[PaymentMessage]
    VO1[ExternalMessageId]
    VO2[MessagePayload]
    ST[MessageStatus]
    DS[MessageIngestionDomainService]
    DE[Domain Events]
  end

  %% Entrants -> Ports entrants -> Use cases
  REST --> P1
  REST --> P2
  REST --> P3
  MQIN --> P4

  P1 --> U1
  P2 --> U2
  P3 --> U3
  P4 --> U4

  %% Use cases -> Domaine
  U1 --> AGG
  U2 --> AGG
  U3 --> AGG
  U4 --> DS
  DS --> AGG
  AGG --> VO1
  AGG --> VO2
  AGG --> ST
  U3 --> DE
  U4 --> DE

  %% Use cases -> Ports sortants
  U1 --> O1
  U2 --> O1
  U4 --> O2
  U3 --> O3
  U3 --> O4
  U4 --> O4

  %% Adaptateurs sortants implementent les ports
  JPA --> O1
  JPA --> O2
  MQOUT --> O3
  EV --> O4

  %% Connexions techniques externes
  JPA --> DB
  MQOUT --> MQ
  MQ --> MQIN
```

## Responsabilités des Couches

### Domaine

Contient les règles métier et les invariants :
- agrégat : `PaymentMessage`
- objets de valeur : `ExternalMessageId`, `MessagePayload`
- modèle d'état : `MessageStatus`
- service de domaine : `MessageIngestionDomainService`
- événements de domaine : `MessageReceivedEvent`, `MessagePublishedEvent`

Aucun couplage avec les frameworks Spring/JPA/MQ ne doit s'y infiltrer.

### Application

Contient les cas d'usage et les contrats de ports :
- cas d'usage : ingestion, publication, liste, consultation
- ports entrants : `IngestMessagePort`, `ListMessagesPort`, `GetMessagePort`, `PublishMessagePort`
- ports sortants : `PaymentMessageQueryPort`, `PaymentMessageStorePort`, `PaymentMessagePublisherPort`, `DomainEventPublisherPort`
- mappage du domaine vers le modèle applicatif (`PaymentMessageRecord` via `MessageRecordMapper`)
- exception applicative (`MessageNotFoundException`)

### Infrastructure

Contient les implémentations techniques :
- contrôleurs API et gestionnaires d'exceptions
- adaptateur listener MQ et adaptateur producer MQ
- entité persistance/référentiel/service/mappage
- adaptateurs implémentant les ports applicatifs
- chargeur de données de bootstrap

## Modèle

```mermaid
erDiagram
  ROUTED_MESSAGES {
    BIGINT id PK
    VARCHAR external_message_id UK
    TEXT payload
    VARCHAR status
    TIMESTAMP received_at
  }
```