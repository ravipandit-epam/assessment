# Backend Service

A production-grade, layered Spring Boot application that ingests user data in multiple formats (JSON, XML, CSV), validates and persists it to Apache GemFire, and provides a generic multi-format data-parsing pipeline.

---

## Features

- **Multi-format Ingestion** — Accept user data as JSON, XML, or CSV via a single REST endpoint
- **Strategy + Factory Pattern** — Pluggable parser architecture; new formats added with zero changes to existing code
- **GemFire Integration** — Primary persistence via Apache GemFire/Geode REST API with configurable retry
- **In-Memory Fallback** — `ConcurrentHashMap`-backed cache client available as a secondary bean
- **Centralized Exception Handling** — `@RestControllerAdvice` maps all exceptions to structured JSON error responses
- **Domain Validation** — Explicit null/blank validation for all required user fields
- **Content-Type Normalization** — Strips charset parameters from `Content-Type` headers before parser lookup
- **Comprehensive Test Suite** — Unit tests + standalone MockMvc integration tests covering all layers

---

## Tech Stack

| Layer            | Technology                                      |
|------------------|-------------------------------------------------|
| Language         | Java 21                                         |
| Framework        | Spring Boot 4.0.5                               |
| Build Tool       | Maven 3.9+ (Maven Wrapper included)             |
| JSON Parsing     | Jackson (`jackson-databind`)                    |
| XML Parsing      | Jackson XML + JAXB (jakarta.xml.bind + Glassfish) |
| CSV Parsing      | Apache Commons CSV 1.11.0                       |
| HTTP Client      | Spring `RestTemplate`                           |
| Validation       | Jakarta Bean Validation                         |
| Cache / Store    | Apache GemFire (REST) + In-memory fallback      |
| Testing          | JUnit 5 + Mockito + Spring MockMvc              |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        HTTP Layer                               │
│   POST /api/v1/users   GET /api/v1/users   POST /api/v1/data/parse │
└────────────┬──────────────────┬─────────────────────┬───────────┘
             │                  │                     │
     ┌───────▼──────┐   ┌───────▼──────┐   ┌─────────▼────────┐
     │UserController│   │UserController│   │  DataController   │
     └───────┬──────┘   └───────┬──────┘   └─────────┬────────┘
             │                  │                     │
     ┌───────▼──────────────────▼──┐     ┌────────────▼──────────┐
     │       UserService           │     │  DataProcessingService │
     │  parse → validate → persist │     │  fetch → parse records │
     └───┬──────────┬─────────┬───┘     └────────┬──────────────┘
         │          │         │                   │
 ┌───────▼───┐ ┌────▼────┐ ┌──▼──────────┐  ┌────▼────────────────┐
 │ParserFactory│ │Validator│ │ CacheClient │  │JSON/XML/CSV DataParser│
 │ (Factory) │ │         │ │  GemFire ✓  │  └─────────────────────┘
 └─────┬─────┘ └─────────┘ │  InMemory   │
       │                   └─────────────┘
 ┌─────▼─────────────┐
 │  InputParser       │  ← Strategy Pattern
 │  ├─ JSON (Jackson) │
 │  ├─ XML (JAXB)     │
 │  └─ CSV (Commons)  │
 └───────────────────┘
```

---

## Project Structure

```
backend-service/
├── pom.xml
├── mvnw / mvnw.cmd
├── API_DOC.md
├── RUN_GUIDE.md
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/backend/
    │   │   ├── BackendServiceApplication.java     # Entry point
    │   │   ├── config/ClientConfig.java           # RestTemplate + properties
    │   │   ├── controller/
    │   │   │   ├── UserController.java            # /api/v1/users
    │   │   │   └── DataController.java            # /api/v1/data
    │   │   ├── dto/
    │   │   │   ├── ParseRequest.java
    │   │   │   └── ParseResponse.java
    │   │   ├── model/
    │   │   │   ├── User.java
    │   │   │   ├── Address.java
    │   │   │   └── DataRecord.java
    │   │   ├── parser/
    │   │   │   ├── InputParser.java               # Strategy interface
    │   │   │   ├── JsonInputParser.java
    │   │   │   ├── XmlInputParser.java
    │   │   │   ├── CsvInputParser.java
    │   │   │   ├── ParserFactory.java             # Factory
    │   │   │   ├── DataParser.java                # Strategy interface
    │   │   │   ├── JsonDataParser.java
    │   │   │   ├── XmlDataParser.java
    │   │   │   └── CsvDataParser.java
    │   │   ├── service/
    │   │   │   ├── UserService.java
    │   │   │   ├── DataProcessingService.java
    │   │   │   ├── impl/
    │   │   │   │   ├── UserServiceImpl.java
    │   │   │   │   └── DataProcessingServiceImpl.java
    │   │   │   └── support/
    │   │   │       ├── ContentTypeResolver.java
    │   │   │       └── HttpContentTypeResolver.java
    │   │   ├── client/
    │   │   │   ├── CacheClient.java               # Interface
    │   │   │   ├── GemfireRestClient.java         # @Primary
    │   │   │   ├── InMemoryCacheClient.java
    │   │   │   ├── GemfireClientProperties.java
    │   │   │   └── ExternalDataClient.java
    │   │   ├── validation/
    │   │   │   ├── UserValidator.java
    │   │   │   └── DefaultUserValidator.java
    │   │   └── exception/
    │   │       ├── GlobalExceptionHandler.java
    │   │       ├── ParsingException.java
    │   │       ├── UnsupportedFormatException.java
    │   │       ├── ValidationException.java
    │   │       └── CacheClientException.java
    │   └── resources/application.yml
    └── test/java/com/example/backend/
        ├── controller/
        │   ├── UserControllerTest.java
        │   ├── UserControllerIntegrationTest.java
        │   └── DataControllerTest.java
        ├── parser/
        │   ├── JsonInputParserTest.java
        │   ├── XmlInputParserTest.java
        │   ├── CsvInputParserTest.java
        │   └── ParserFactoryTest.java
        ├── service/
        │   ├── UserServiceImplTest.java
        │   └── DataProcessingServiceImplTest.java
        └── client/
            └── GemfireRestClientTest.java
```

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+ _(or use the included `mvnw`/`mvnw.cmd` wrapper — no Maven install required)_
- Apache GemFire/Geode with REST API enabled _(optional — see Configuration)_

### Installation

```bash
git clone <repository-url>
cd UBS
```

### Build

```bash
# Windows
.\mvnw.cmd clean install

# macOS / Linux
./mvnw clean install
```

### Running the Application

```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run

# Or run the JAR directly
java -jar target/backend-service-0.0.1-SNAPSHOT.jar
```

The service starts at **`http://localhost:8080`**.

---

## API Documentation

See **[API_DOC.md](./API_DOC.md)** for the full API reference including:
- Endpoint definitions and HTTP methods
- Request/response examples (JSON, XML, CSV)
- Data models
- Error response catalogue
- Content-Type handling details

For setup instructions and Postman/cURL usage, see **[RUN_GUIDE.md](./RUN_GUIDE.md)**.

---

## Usage Examples

### Create a User (JSON)

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ravi",
    "email": "ravi@example.com",
    "address": {
      "street": "123 Main Street",
      "city": "Mumbai",
      "country": "India"
    }
  }'
```

**Response `201 Created`:**
```json
{
  "username": "ravi",
  "email": "ravi@example.com",
  "address": { "street": "123 Main Street", "city": "Mumbai", "country": "India" }
}
```

### Get All Users

```bash
curl http://localhost:8080/api/v1/users
```

### Parse Data Records

```bash
curl -X POST http://localhost:8080/api/v1/data/parse \
  -H "Content-Type: application/json" \
  -d '{"format":"csv","payload":"id,name,source\n1,Alice,csv"}'
```

**Response `200 OK`:**
```json
{ "format": "csv", "count": 1, "records": [{ "id": "1", "name": "Alice", "source": "csv" }] }
```

---

## Testing

### Run All Tests

```bash
# Windows
.\mvnw.cmd test

# macOS / Linux
./mvnw test
```

### Test Coverage Summary

| Test Class                          | Type              | Scope                                      |
|-------------------------------------|-------------------|--------------------------------------------|
| `UserControllerTest`                | Unit              | Controller response codes and delegation   |
| `UserControllerIntegrationTest`     | MockMvc (standalone) | Full HTTP flow + exception handler      |
| `DataControllerTest`                | Unit              | Parse endpoint delegation                  |
| `JsonInputParserTest`               | Unit              | Valid and invalid JSON parsing             |
| `XmlInputParserTest`                | Unit              | Valid and invalid XML parsing              |
| `CsvInputParserTest`                | Unit              | Valid CSV and empty input error            |
| `ParserFactoryTest`                 | Unit              | Parser resolution and unsupported type     |
| `UserServiceImplTest`               | Unit              | Full flow + validation failure scenarios   |
| `DataProcessingServiceImplTest`     | Unit (`@ExtendWith`) | Format routing + unsupported format     |
| `GemfireRestClientTest`             | Unit              | Save/get + retry logic + exhaustion error  |

---

## Design Principles

### SOLID Principles Applied

| Principle                    | How It's Applied                                                                   |
|------------------------------|------------------------------------------------------------------------------------|
| **Single Responsibility**    | Parsing, validation, caching, and HTTP handling are fully separated                |
| **Open / Closed**            | `ParserFactory` is open for extension — add a new `InputParser` bean, nothing else changes |
| **Liskov Substitution**      | `GemfireRestClient` and `InMemoryCacheClient` are interchangeable via `CacheClient` |
| **Interface Segregation**    | Fine-grained interfaces: `InputParser`, `DataParser`, `CacheClient`, `UserValidator`, `ContentTypeResolver` |
| **Dependency Inversion**     | Controllers and services depend on interfaces; implementations are injected by Spring |

### Design Patterns Used

| Pattern            | Location                                            |
|--------------------|-----------------------------------------------------|
| **Strategy**       | `InputParser` and `DataParser` hierarchies          |
| **Factory**        | `ParserFactory` — auto-registry via Spring DI       |
| **Template Method** | `GemfireRestClient.executeWithRetry()` — retry skeleton |
| **Retry**          | `GemfireRestClient` — configurable retry with delay |

---

## Future Improvements

- **Spring Profiles** — `dev` profile that activates `InMemoryCacheClient` as primary to remove GemFire dependency during development
- **Pagination** — `GET /api/v1/users` currently returns all records; add page/size parameters
- **Batch CSV Ingest** — `POST /api/v1/users` currently processes only the first CSV row; extend to multi-user batch
- **Authentication** — Add Spring Security for JWT or Basic Auth
- **OpenAPI / Swagger** — Integrate `springdoc-openapi` for auto-generated interactive documentation
- **Actuator** — Add Spring Boot Actuator for production health, metrics, and info endpoints
- **ExternalDataClient** — Implement actual external data source integration (currently a pass-through stub)
- **Structured Logging** — Add trace/correlation IDs for distributed tracing

---

## Contribution Guidelines

1. Fork the repository and create a feature branch from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. Write tests for all new behavior — maintain the existing unit/MockMvc test pattern.
3. Ensure all tests pass before opening a Pull Request:
   ```bash
   ./mvnw test
   ```
4. Follow existing package and naming conventions.
5. Keep PRs focused — one feature or fix per PR.
6. Add or update documentation (`API_DOC.md`, `RUN_GUIDE.md`) if your change affects the API or setup.

---

## License

This project does not currently define a license. All rights reserved by the author until a license is specified.

---

*Built with Spring Boot 4.0.5 · Java 21 · Apache Commons CSV · Jackson · JAXB · Apache GemFire*
