# Backend Service — API Documentation

## 1. Overview

The **Backend Service** is a Spring Boot application that provides REST APIs for ingesting user data in multiple formats (JSON, XML, CSV) and persisting it to an Apache GemFire cache. It also offers a generic data-parsing endpoint that accepts structured payloads and returns parsed records.

**Base URL:** `http://localhost:8080`

---

## 2. Endpoints

### 2.1 User Ingest API

#### `POST /api/v1/users`

Parses a raw user payload based on the `Content-Type` header, validates the result, and persists it to the cache (GemFire).

**Supported Content-Types:**

| Content-Type        | Parser Used      |
|---------------------|------------------|
| `application/json`  | Jackson          |
| `application/xml`   | JAXB             |
| `text/csv`          | Apache Commons CSV |
| `text/plain`        | Resolved at runtime (mapped to `text/plain`) |

**Headers:**

| Header         | Required | Description                          |
|----------------|----------|--------------------------------------|
| `Content-Type` | Yes      | Determines which parser is used      |

---

**Request Body — JSON Example:**

```json
{
  "username": "ravi",
  "email": "ravi@example.com",
  "address": {
    "street": "123 Main Street",
    "city": "Mumbai",
    "country": "India"
  }
}
```

**Request Body — XML Example:**

```xml
<user>
  <username>ravi</username>
  <email>ravi@example.com</email>
  <address>
    <street>123 Main Street</street>
    <city>Mumbai</city>
    <country>India</country>
  </address>
</user>
```

**Request Body — CSV Example:**

```csv
username,email,street,city,country
ravi,ravi@example.com,123 Main Street,Mumbai,India
```

> **Note:** Only the first data row is parsed for CSV input. The header row is required.

---

**Success Response:**

- **Status:** `201 Created`
- **Content-Type:** `application/json`

```json
{
  "username": "ravi",
  "email": "ravi@example.com",
  "address": {
    "street": "123 Main Street",
    "city": "Mumbai",
    "country": "India"
  }
}
```

**Error Responses:**

| Status | Condition                                | Body                                                        |
|--------|------------------------------------------|-------------------------------------------------------------|
| 400    | Invalid/malformed input                  | `{"error": "Invalid JSON input for User parsing."}`         |
| 400    | Missing required field (e.g., email)     | `{"error": "Email is required."}`                           |
| 400    | Unsupported content type in parser       | `{"error": "Unsupported content type: text/html"}`          |
| 415    | Content-Type not in the accepted list    | `{"error": "Unsupported Content-Type."}`                    |
| 503    | GemFire unavailable after retries        | `{"error": "Failed to save user to GemFire."}`              |
| 500    | Unexpected server error                  | `{"error": "Unexpected server error."}`                     |

---

#### `GET /api/v1/users`

Retrieves all users currently stored in the cache.

**Headers:** None required.

**Success Response:**

- **Status:** `200 OK`
- **Content-Type:** `application/json`

```json
[
  {
    "username": "ravi",
    "email": "ravi@example.com",
    "address": {
      "street": "123 Main Street",
      "city": "Mumbai",
      "country": "India"
    }
  },
  {
    "username": "alice",
    "email": "alice@example.com",
    "address": {
      "street": "456 Oak Ave",
      "city": "London",
      "country": "UK"
    }
  }
]
```

**Error Responses:**

| Status | Condition                             | Body                                                            |
|--------|---------------------------------------|-----------------------------------------------------------------|
| 503    | GemFire unavailable after retries     | `{"error": "Failed to retrieve users from GemFire."}`           |
| 500    | Unexpected server error               | `{"error": "Unexpected server error."}`                         |

---

### 2.2 Data Processing API

#### `GET /api/v1/data/health`

Health-check endpoint.

**Success Response:**

- **Status:** `200 OK`
- **Content-Type:** `text/plain`
- **Body:** `Backend service is running`

---

#### `POST /api/v1/data/parse`

Parses a raw data payload into a list of `DataRecord` objects based on the specified format.

**Headers:**

| Header         | Required | Value              |
|----------------|----------|--------------------|
| `Content-Type` | Yes      | `application/json` |

**Request Body:**

```json
{
  "format": "json",
  "payload": "[{\"id\":\"1\",\"name\":\"Alice\",\"source\":\"json\"},{\"id\":\"2\",\"name\":\"Bob\",\"source\":\"json\"}]"
}
```

The `format` field accepts: `"json"`, `"xml"`, or `"csv"`.

**Request — XML Payload Example:**

```json
{
  "format": "xml",
  "payload": "<records><record><id>1</id><name>Alice</name><source>xml</source></record></records>"
}
```

**Request — CSV Payload Example:**

```json
{
  "format": "csv",
  "payload": "id,name,source\n1,Alice,csv\n2,Bob,csv"
}
```

---

**Success Response:**

- **Status:** `200 OK`
- **Content-Type:** `application/json`

```json
{
  "format": "json",
  "count": 2,
  "records": [
    {
      "id": "1",
      "name": "Alice",
      "source": "json"
    },
    {
      "id": "2",
      "name": "Bob",
      "source": "json"
    }
  ]
}
```

**Error Responses:**

| Status | Condition                                 | Body                                             |
|--------|-------------------------------------------|--------------------------------------------------|
| 400    | `format` or `payload` is blank            | `{"error": "Invalid request payload."}`          |
| 400    | Unsupported format (e.g., `"yaml"`)       | `{"error": "Unsupported format: yaml"}`          |
| 500    | Unexpected server error                   | `{"error": "Unexpected server error."}`          |

---

## 3. Data Models

### 3.1 User

| Field      | Type    | Required | Description          |
|------------|---------|----------|----------------------|
| `username` | String  | Yes      | Unique username      |
| `email`    | String  | Yes      | User's email address |
| `address`  | Address | Yes      | User's address       |

XML root element: `<user>`. Property order: `username`, `email`, `address`.

### 3.2 Address

| Field     | Type   | Required | Description       |
|-----------|--------|----------|-------------------|
| `street`  | String | Yes      | Street address    |
| `city`    | String | Yes      | City              |
| `country` | String | Yes      | Country           |

Property order: `street`, `city`, `country`.

### 3.3 DataRecord

| Field    | Type   | Description                  |
|----------|--------|------------------------------|
| `id`     | String | Record identifier            |
| `name`   | String | Record name                  |
| `source` | String | Source system/format         |

XML root element: `<record>`.

### 3.4 ParseRequest (DTO)

| Field     | Type   | Validation  | Description                           |
|-----------|--------|-------------|---------------------------------------|
| `format`  | String | `@NotBlank` | Format identifier: `json`, `xml`, `csv` |
| `payload` | String | `@NotBlank` | Raw data payload string               |

### 3.5 ParseResponse (DTO)

| Field     | Type              | Description                      |
|-----------|-------------------|----------------------------------|
| `format`  | String            | The normalized format used       |
| `count`   | int               | Number of records parsed         |
| `records` | List\<DataRecord\> | Parsed records                   |

---

## 4. Content-Type Handling

The `Content-Type` header sent with `POST /api/v1/users` is processed as follows:

1. **Spring filters** the request — only `application/json`, `application/xml`, `text/csv`, and `text/plain` are accepted. Any other type returns `415 Unsupported Media Type`.
2. **`HttpContentTypeResolver.normalize()`** strips parameters (e.g., `charset=UTF-8`) from the Content-Type, returning only `type/subtype` (e.g., `application/json`).
3. **`ParserFactory.getParser()`** looks up the normalized content type in its registry (case-insensitive) and returns the appropriate `InputParser` implementation.
4. If no parser is registered for the content type, an `UnsupportedFormatException` is thrown → `400 Bad Request`.

---

## 5. Architecture Summary

```
┌─────────────┐     ┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│  Controller  │────▶│   Service    │────▶│  ParserFactory   │────▶│ InputParser  │
│              │     │              │     │  (Factory)       │     │ (Strategy)   │
│ UserController│     │ UserService  │     └──────────────────┘     │ ├─ JSON      │
│ DataController│     │ DataService  │                              │ ├─ XML       │
└─────────────┘     └──────┬───────┘                              │ └─ CSV       │
                           │                                       └──────────────┘
                    ┌──────▼───────┐     ┌──────────────┐
                    │  Validator   │     │ CacheClient  │
                    │              │     │ (Interface)  │
                    │ UserValidator │     │ ├─ GemFire   │ ◀──── GemFire REST API
                    └──────────────┘     │ └─ InMemory  │
                                         └──────────────┘
```

### Layers

| Layer          | Responsibility                                             |
|----------------|-------------------------------------------------------------|
| **Controller** | Accept HTTP requests, delegate to services, return responses |
| **Service**    | Business logic orchestration (parse → validate → persist)   |
| **Parser**     | Data format transformation (Strategy pattern)               |
| **Factory**    | Parser selection based on content type / format string       |
| **Validator**  | Domain model validation                                      |
| **Client**     | External system communication (GemFire REST, in-memory)     |
| **Exception**  | Centralized error handling via `@RestControllerAdvice`       |
| **Config**     | Spring bean configuration and externalized properties        |

---

## 6. Assumptions & Limitations

1. **Single-user CSV:** The `POST /api/v1/users` CSV parser only reads the **first data row**. Multi-user CSV batch ingestion is not supported on this endpoint.
2. **GemFire dependency:** The primary cache client requires a running GemFire/Geode instance. Without it, all save/get operations will fail after retries with `503 Service Unavailable`. The `InMemoryCacheClient` exists as a bean but is not `@Primary`.
3. **ExternalDataClient is a stub:** `fetchRawPayload()` returns the input unchanged. This is a placeholder for future external data source integration.
4. **DataParser silent failures:** `JsonDataParser`, `XmlDataParser`, and `CsvDataParser` return an empty list on parse failure instead of throwing exceptions.
5. **No authentication/authorization:** All endpoints are publicly accessible.
6. **No pagination:** `GET /api/v1/users` returns all users with no pagination support.
7. **Username as key:** Users are keyed by `username` in both GemFire and in-memory cache. Duplicate usernames will overwrite existing entries.
8. **Lombok declared but unused:** Lombok is in `pom.xml` as optional but no Lombok annotations are observed in the source code.
