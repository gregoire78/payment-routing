# payment-routing

Pour travailler sur ce projet, j'utilise un devcontainer VS Code.
J'ai également utilisé de l'IA génératif pour les tests et du "refactoring"de code.

Le backend utilise une architecture hexagonale.
Le frontend utilise une architecure atomique.

## Structure

- [backend](backend): application Spring Boot 4 avec API REST, JPA et configuration IBM MQ
- [frontend](frontend): application Angular 22 minimale pour consulter les messages et publier
- [docker-compose.yml](docker-compose.yml): stack locale backend/frontend/postgres/ibmmq

## Fonctionnalités disponibles

- stockage relationnel des messages dans PostgreSQL
- endpoint REST de consultation: GET /api/messages et GET /api/messages/{id}
- endpoint REST de publication de démonstration: POST /api/messages/publish
- consommation IBM MQ via listener JMS configurable
- listener IBM MQ idempotent désactivable par propriété
- écran Angular simple de consultation
- seed automatique d'un message de démonstration au démarrage

## Prérequis

- Java 21+
- Maven 3.8+
- Node 22+
- npm 10+
- PostgreSQL et IBM MQ

Le devcontainer fourni contient PostgreSQL et IBM MQ via [docker-compose du devcontainer](.devcontainer/docker-compose.yml).

## Utilisation du devcontainer

Le projet inclut un environnement de développement VS Code dans [.devcontainer/devcontainer.json](.devcontainer/devcontainer.json).

Cet environnement démarre:

- un conteneur de travail Linux Debian
- PostgreSQL
- IBM MQ

Outils déjà installés dans le conteneur de développement:

- Java
- Maven 3.8.6
- Node 22
- npm 10.9.0

### Ouvrir le projet dans le devcontainer

1. Ouvrir le repository dans VS Code.
2. Lancer la commande Reopen in Container.
3. Attendre le démarrage complet des services du devcontainer.

Le dossier de travail dans le conteneur est /workspaces/payment-routing.

### Services disponibles dans le devcontainer

- PostgreSQL accessible via l'hôte db sur le port 5432
- IBM MQ accessible via l'hôte ibmmq sur le port 1414

Variables déjà injectées dans le conteneur applicatif:

- POSTGRES_HOSTNAME=db
- POSTGRES_DB=postgres
- POSTGRES_USER=postgres
- POSTGRES_PASSWORD=postgres

### Lancer l'application depuis le devcontainer

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm start
```

### Remarque sur les ports

- 8080 pour le backend
- 4200 pour le frontend
- 5432 pour PostgreSQL
- 1414 pour IBM MQ

## Démarrage en développement

### 1. Démarrer le backend

Depuis [backend](backend):

```bash
mvn spring-boot:run
```

Le backend démarre par défaut sur http://localhost:8080.

Endpoints utiles:

- http://localhost:8080/actuator/health
- http://localhost:8080/api/messages

### 2. Démarrer le frontend

Depuis [frontend](frontend):

```bash
npm install
npm start
```

Le frontend démarre par défaut sur http://localhost:4200.

Le proxy Angular redirige automatiquement /api vers http://localhost:8080.

## API

### Vérifier la santé du backend

```bash
curl http://localhost:8080/actuator/health
```

### Vérifier les messages

```bash
curl http://localhost:8080/api/messages
```

### Publier un message de démonstration dans MQ via l'API

```bash
curl -X POST http://localhost:8080/api/messages/publish \
	-H 'Content-Type: application/json' \
	-d '{
		"externalMessageId": "MANUAL-DEMO-001",
		"payload": "{\"type\":\"PAYMENT\",\"amount\":150.25,\"currency\":\"EUR\"}"
	}'
```

Puis vérifier qu'il est visible via l'API de consultation:

```bash
curl http://localhost:8080/api/messages
```

## Variables

Backend:

- POSTGRES_HOSTNAME
- POSTGRES_DB
- POSTGRES_USER
- POSTGRES_PASSWORD
- IBM_MQ_QUEUE_MANAGER
- IBM_MQ_CHANNEL
- IBM_MQ_CONN_NAME
- IBM_MQ_QUEUE_NAME
- IBM_MQ_USER
- IBM_MQ_PASSWORD
- APP_MQ_LISTENER_ENABLED
- APP_MQ_LISTENER_CONCURRENCY
- APP_CORS_ALLOWED_ORIGINS
- APP_DEMO_SEED_ENABLED

Le listener MQ est activé par défaut en local. Pour le désactiver:

```bash
cd backend
APP_MQ_LISTENER_ENABLED=false mvn spring-boot:run
```

## Démarrage recommandé avec le devcontainer

1. Ouvrir le repository dans VS Code.
2. Lancer la commande Reopen in Container.
3. Attendre le démarrage complet des services du devcontainer.

La stack applicative peut aussi être lancée localement si besoin.

La stack expose:

- frontend: http://localhost:4200
- backend: http://localhost:8080
- postgres: localhost:5432
- ibmmq: localhost:1414
