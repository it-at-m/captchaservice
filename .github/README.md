# CaptchaService

[![Documentation][documentation-shield]][documentation]
[![New issue][new-issue-shield]][new-issue]
[![Made with love by it@M][made-with-love-shield]][itm-opensource]
[![GitHub license][license-shield]][license]

A Spring Boot microservice that provides proof-of-work CAPTCHA challenges using the [ALTCHA library](https://altcha.org/). This service offers an alternative to traditional image-based CAPTCHAs with adaptive difficulty management and multi-tenant support.

## Table of Contents

- [Architecture](#architecture)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Database](#database)
- [Monitoring](#monitoring)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Architecture

```mermaid
graph TB
    %% External Client
    Client

    %% External Services
    subgraph "External Services"
        PostgreSQL[(PostgreSQL<br/>Database)]
    end

    %% Main CaptchaService Application
    subgraph "CaptchaService Application"

        %% Monitoring & Management
        subgraph "Monitoring"
            Actuator["**Spring Actuator**<br/>/actuator/info<br/>/actuator/health<br/>/actuator/metrics"]
        end

        %% API Endpoints Detail
        subgraph "CaptchaController"
            ChallengeEndpoint["**postChallenge()**<br/>Create CAPTCHA Challenge"]
            VerifyEndpoint["**postVerify()**<br/>Verify CAPTCHA Solution"]
        end

        %% Service Layer
        subgraph "Service Layer"
            CaptchaService["**CaptchaService**<br/>Core CAPTCHA Logic"]
            DifficultyService["**DifficultyService**<br/>Adaptive Difficulty Management"]
            SiteAuthService["**SiteAuthService**<br/>Site Key/Secret Validation"]
            SourceAddressService["**SourceAddressService**<br/>IP Address Validation"]
            ExpiredDataService["**ExpiredDataService**<br/>Cleanup Scheduler"]
        end

        %% Data Layer
        subgraph "Data Layer"
            CaptchaRequestRepo["**CaptchaRequestRepository**<br/>JPA Repository"]
            InvalidatedPayloadRepo["**InvalidatedPayloadRepository**<br/>JPA Repository"]

            subgraph "JPA Entities"
                CaptchaRequestEntity["**CaptchaRequest**<br/>Entity"]
                InvalidatedPayloadEntity["**InvalidatedPayload**<br/>Entity"]
            end
        end

        %% Properties/Configuration
        subgraph "Configuration Properties"
            CaptchaProperties["**CaptchaProperties**<br/>HMAC Key, Sites Config"]
            CaptchaSite["**CaptchaSite**<br/>Site-specific Settings"]
            DifficultyItem["**DifficultyItem**<br/>Difficulty Mappings"]
        end
    end

    %% Request Flow
    Client --&gt;|POST /api/v1/challenge| ChallengeEndpoint
    Client --&gt;|POST /api/v1/verify| VerifyEndpoint
    Client --&gt; Monitoring

    %% Controller to Services
    ChallengeEndpoint --&gt; CaptchaService
    VerifyEndpoint --&gt; CaptchaService
    ChallengeEndpoint --&gt; SiteAuthService
    VerifyEndpoint --&gt; SiteAuthService
    ChallengeEndpoint --&gt; SourceAddressService

    %% Service Interactions
    CaptchaService --&gt; DifficultyService
    CaptchaService --&gt; CaptchaRequestRepo
    CaptchaService --&gt; InvalidatedPayloadRepo
    CaptchaService --&gt; AltchaLib

    DifficultyService --&gt; CaptchaRequestRepo
    ExpiredDataService --&gt;|Scheduled Cleanup| CaptchaRequestRepo
    ExpiredDataService --&gt;|Scheduled Cleanup| InvalidatedPayloadRepo

    %% Data Layer
    CaptchaRequestRepo --&gt; CaptchaRequestEntity
    InvalidatedPayloadRepo --&gt; InvalidatedPayloadEntity
    CaptchaRequestEntity -.->|JPA/Hibernate| PostgreSQL
    InvalidatedPayloadEntity -.->|JPA/Hibernate| PostgreSQL

    %% Configuration Dependencies
    CaptchaService -.->|Uses| CaptchaProperties
    SiteAuthService -.->|Uses| CaptchaProperties
    SourceAddressService -.->|Uses| CaptchaProperties
    DifficultyService -.->|Uses| CaptchaProperties

    %% Database Migration
    Flyway -.->|Schema Management| PostgreSQL

    class CaptchaService,DifficultyService,SiteAuthService,SourceAddressService,ExpiredDataService service
    class CaptchaRequestRepo,InvalidatedPayloadRepo,CaptchaRequestEntity,InvalidatedPayloadEntity data
    class PostgreSQL,Client external
    class ChallengeEndpoint,VerifyEndpoint endpoint
    class CaptchaProperties,CaptchaSite,DifficultyItem properties
```

## Features

- **Proof-of-Work CAPTCHA**: Uses ALTCHA library for crypto-based challenge verification
- **Adaptive Difficulty**: Automatically adjusts challenge difficulty based on request patterns
- **Multi-Tenant Support**: Site-specific configuration with individual keys and secrets
- **Source Address Validation**: IP-based filtering and network address validation
- **Scheduled Cleanup**: Automatic removal of expired challenges and invalidated payloads
- **Monitoring**: Comprehensive health checks and metrics via Spring Actuator
- **Database Persistence**: PostgreSQL storage with automated Flyway migrations

## Prerequisites

- Java 21 or later
- Maven 3.8+
- PostgreSQL 16+
- Docker and Docker Compose (for local development)

## Quick Start

1. **Clone the repository**

   ```bash
   git clone https://github.com/it-at-m/captchaservice.git
   cd captchaservice
   ```

2. **Start the development stack**

   ```bash
   cd stack
   docker compose up -d
   ```

3. **Build and run the application**

   ```bash
   cd captchaservice-backend
   bash runLocal.sh
   ```

4. **Verify the service is running**

   ```bash
   curl http://localhost:8080/actuator/health
   ```

## Configuration

### Environment Variables

| Variable                                | Description                    | Default                                           |
| --------------------------------------- | ------------------------------ | ------------------------------------------------- |
| `SPRING_DATASOURCE_URL`                 | PostgreSQL connection URL      | `jdbc:postgresql://localhost:5432/captchaservice` |
| `SPRING_DATASOURCE_USERNAME`            | Database username              | -                                                 |
| `SPRING_DATASOURCE_PASSWORD`            | Database password              | -                                                 |
| `CAPTCHA_HMAC_KEY`                      | HMAC key for challenge signing | -                                                 |
| `CAPTCHA_CAPTCHA_TIMEOUT_SECONDS`       | Challenge validity period      | `300`                                             |
| `CAPTCHA_SOURCE_ADDRESS_WINDOW_SECONDS` | Source address tracking window | `3600`                                            |

### Site Configuration

Configure multiple sites in your `application.yml`:

```yaml
captcha:
  hmac-key: secret # HMAC key for signing challenges
  captcha-timeout-seconds: 300 # How long a CAPTCHA challenge is valid
  source-address-window-seconds: 3600 # How long a source address is stored
  sites:
    site1: # Site key for site1
      site-secret: "secret1" # Site secret for site1
      max-verifies-per-payload: 1 # How many times a payload can be verified
      whitelisted_source-addresses:
        - "192.0.2.0/24" # Whitelisted IP address range
    site2:
      site-secret: "secret2"
      whitelisted_source-addresses:
        - "192.0.2.0/24" # Whitelisted IP address range
      difficulty-map:
        - min-visits: 1 # From the first visit on...
          max-number: 1000 # ...the difficulty is 1000
        - min-visits: 10 # From the 10th visit on...
          max-number: 10000 # ...the difficulty is 10000
```

## API Documentation

### Create Challenge

**POST** `/api/v1/captcha/challenge`

Creates a new CAPTCHA challenge for the specified site.

**Request Body:**

```json
{
  "siteKey": "site1",
  "siteSecret": "secret1",
  "clientAddress": "192.168.1.100"
}
```

**Response:**

```json
{
  "algorithm": "SHA-256",
  "challenge": "abc123...",
  "maxNumber": 1000,
  "salt": "def456...",
  "signature": "ghi789..."
}
```

### Verify Solution

**POST** `/api/v1/captcha/verify`

Verifies a CAPTCHA solution payload.

**Request Body:**

```json
{
  "siteKey": "site1",
  "siteSecret": "secret1",
  "payload": {
    "algorithm": "SHA-256",
    "challenge": "abc123...",
    "number": 542,
    "salt": "def456...",
    "signature": "ghi789...",
    "took": 4400
  }
}
```

**Response:**

```json
{
  "valid": true
}

// or if the solution is invalid

{
  "valid": false
}
```

### Error Responses

**401 Unauthorized** - Invalid site credentials

```json
{
  "status": 401,
  "error": "Authentication Error"
}
```

**400 Bad Request** - Invalid request format

```json
{
  "status": 400,
  "error": "Bad Request"
}
```

## Database

### Schema Management

Database schema is managed using Flyway migrations located in `src/main/resources/db/migration/`

### Manual Migration

```bash
# Run migrations manually
mvn flyway:migrate

# Check migration status
mvn flyway:info

# Validate migrations
mvn flyway:validate
```

## Monitoring

### Health Checks

- **Liveness**: `GET /actuator/health/liveness`
- **Readiness**: `GET /actuator/health/readiness`
- **Overall Health**: `GET /actuator/health`

### Application Information

- **Info Endpoint**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`

### Metrics

Prometheus metrics available at `/actuator/prometheus`:

- Application metrics
- JVM metrics
- Database connection pool metrics
- Custom CAPTCHA metrics

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

To learn more about how you can contribute, please read our [contribution documentation][contribution-documentation].

## License

Distributed under the MIT License. See [LICENSE][license] file for more information.

## Contact

it@M - <opensource@muenchen.de>
