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

Les **ports** sont des interfaces définies dans la couche application :

| Port | Responsabilité | Adaptateurs |
|------|----------------|-------------|
| `PaymentMessageQueryPort` | Requêtes en lecture | `JpaPaymentMessageQueryAdapter` |
| `PaymentMessageStorePort` | Persistance en écriture | `JpaPaymentMessageStoreAdapter` |
| `IngestMessagePort` | Point d'entrée applicatif pour l'ingestion | `IngestMessageUseCase` |
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
  %% Acteurs externes
  client[Client REST]
  mqIn[(Queue MQ Entrée)]
  mqOut[(Queue MQ Sortie)]
  db[(Base de données: routed_messages)]

  %% Limites API
  subgraph API[Infrastructure API]
    controller[MessageController]
    request[PublishMessageRequest]
    view[PaymentMessageResponse]
    exHandler[MessageExceptionHandler]
  end

  %% Limites Messagerie
  subgraph MSG[Infrastructure Messagerie]
    listener[MqMessageListenerAdapter]
    producer[MqMessageProducerAdapter]
  end

  %% Limites Bootstrap
  subgraph BOOT[Infrastructure Bootstrap]
    bootstrap[MessageBootstrapDataLoader]
  end

  %% Limites Adaptateurs
  subgraph ADAPTERS[Infrastructure Adaptateurs]
    queryAdapter[JpaPaymentMessageQueryAdapter]
    storeAdapter[JpaPaymentMessageStoreAdapter]
    publisherAdapter[MqPaymentMessagePublisherAdapter]
    eventAdapter[SpringDomainEventPublisherAdapter]
  end

  %% Limites Persistance
  subgraph PERSIST[Infrastructure Persistance]
    repo[PersistedMessageRepository]
    persistSvc[MessagePersistenceService]
    mapper[RoutedMessageMapper]
    entity[PersistedMessageEntity]
  end

  %% Limites Couche Application
  subgraph APP[Couche Application]
    ucList[ListMessagesUseCase]
    ucGet[GetMessageUseCase]
    ucIngest[IngestMessageUseCase]
    ucPublish[PublishMessageUseCase]
    ingestPort[(IngestMessagePort)]
    recMapper[MessageRecordMapper]
    rec[PaymentMessageRecord]
    exNotFound[MessageNotFoundException]
    ports[(Ports: PaymentMessageQueryPort / PaymentMessageStorePort / PaymentMessagePublisherPort / DomainEventPublisherPort)]
    eventLogger[MessageDomainEventLogger]
  end

  %% Limites Domaine
  subgraph DOMAIN[Couche Domaine]
    aggregate[PaymentMessage]
    voExt[ExternalMessageId]
    voPayload[MessagePayload]
    status[MessageStatus]
    ingestSvc[MessageIngestionDomainService]
    ingestResult[MessageIngestionResult]
    eventBase[DomainEvent]
    evtReceived[MessageReceivedEvent]
    evtPublished[MessagePublishedEvent]
  end

  %% Flux lecture REST
  client --> controller
  controller --> ucList
  controller --> ucGet
  ucList --> ports
  ucGet --> ports
  queryAdapter -.implements.-> ports
  queryAdapter --> repo
  queryAdapter --> mapper
  mapper --> aggregate
  ucList --> recMapper
  ucGet --> recMapper
  recMapper --> rec
  controller --> view
  ucGet --> exNotFound
  exHandler --> exNotFound

  %% Flux publication REST
  controller --> request
  controller --> ucPublish
  ucPublish --> ports
  publisherAdapter -.implements.-> ports
  publisherAdapter --> producer
  producer --> mqOut
  ucPublish --> evtPublished

  %% Flux ingestion MQ
  mqIn --> listener
  listener --> ingestPort
  ingestPort --> ucIngest
  bootstrap --> ingestPort
  ucIngest --> ingestSvc
  ingestSvc --> ingestResult
  ingestSvc --> aggregate
  ucIngest --> ports
  storeAdapter -.implements.-> ports
  storeAdapter --> persistSvc
  persistSvc --> repo
  persistSvc --> entity
  repo --> db
  ucIngest --> evtReceived

  %% Flux publication événements domaine
  ucIngest --> eventAdapter
  ucPublish --> eventAdapter
  eventAdapter -.implements.-> ports
  eventAdapter --> eventBase
  eventBase --> evtReceived
  eventBase --> evtPublished
  eventLogger --> evtReceived
  eventLogger --> evtPublished

  %% Internals du domaine
  aggregate --> voExt
  aggregate --> voPayload
  aggregate --> status
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
- ports : ingestion/requête/stockage/publication/publication événements
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