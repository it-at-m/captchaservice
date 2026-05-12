# Architecture

CaptchaService is a single Spring Boot application backed by a PostgreSQL database. All public traffic enters through `CaptchaController`, which delegates to a thin service layer; JPA repositories persist the challenge and invalidation state to PostgreSQL.

## Component Diagram

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
    Client -->|POST /api/v1/challenge| ChallengeEndpoint
    Client -->|POST /api/v1/verify| VerifyEndpoint
    Client --> Monitoring

    %% Controller to Services
    ChallengeEndpoint --> CaptchaService
    VerifyEndpoint --> CaptchaService
    ChallengeEndpoint --> SiteAuthService
    VerifyEndpoint --> SiteAuthService
    ChallengeEndpoint --> SourceAddressService

    %% Service Interactions
    CaptchaService --> DifficultyService
    CaptchaService --> CaptchaRequestRepo
    CaptchaService --> InvalidatedPayloadRepo
    CaptchaService --> AltchaLib

    DifficultyService --> CaptchaRequestRepo
    ExpiredDataService -->|Scheduled Cleanup| CaptchaRequestRepo
    ExpiredDataService -->|Scheduled Cleanup| InvalidatedPayloadRepo

    %% Data Layer
    CaptchaRequestRepo --> CaptchaRequestEntity
    InvalidatedPayloadRepo --> InvalidatedPayloadEntity
    CaptchaRequestEntity -.->|JPA/Hibernate| PostgreSQL
    InvalidatedPayloadEntity -.->|JPA/Hibernate| PostgreSQL

    %% Configuration Dependencies
    CaptchaService -.->|Uses| CaptchaProperties
    SiteAuthService -.->|Uses| CaptchaProperties
    SourceAddressService -.->|Uses| CaptchaProperties
    DifficultyService -.->|Uses| CaptchaProperties

    %% Database Migration
    Flyway -.->|Schema Management| PostgreSQL
```

## Components

- **`CaptchaController`** — REST entry point. Exposes `POST /api/v1/captcha/challenge` and `POST /api/v1/captcha/verify`.
- **`CaptchaService`** — core challenge generation and verification, wrapping the ALTCHA library.
- **`DifficultyService`** — computes the proof-of-work difficulty for a given site and source address, based on recent traffic.
- **`SiteAuthService`** — validates the `siteKey` / `siteSecret` pair sent by every client.
- **`SourceAddressService`** — validates the client IP against the per-site allow-list / observation window.
- **`ExpiredDataService`** — scheduled job that removes expired challenges and invalidated payloads.
- **JPA layer** — two repositories (`CaptchaRequestRepository`, `InvalidatedPayloadRepository`) backed by Hibernate entities and PostgreSQL.
- **Flyway** — manages schema migrations under `src/main/resources/db/migration/`.
- **Spring Actuator** — exposes `/actuator/health`, `/actuator/info`, `/actuator/metrics`, and `/actuator/prometheus`.

## Request Flow

1. The client POSTs `siteKey`, `siteSecret`, and `clientAddress` to `/api/v1/captcha/challenge`.
2. `SiteAuthService` authenticates the site, `SourceAddressService` validates the IP, and `DifficultyService` picks the right difficulty based on the per-site difficulty map and recent visit count.
3. `CaptchaService` asks ALTCHA to generate a signed challenge, persists a `CaptchaRequest`, and returns the challenge.
4. The client solves the proof-of-work and POSTs the solved payload to `/api/v1/captcha/verify`.
5. `CaptchaService` verifies the signature and HMAC, marks the payload invalidated (so it cannot be reused), and returns `{ "valid": true | false }`.
