# Backend Service — Run Guide

## 1. Project Overview

A Spring Boot service that ingests user data (JSON/XML/CSV), validates it, and persists to GemFire. Also provides a generic data-parsing endpoint.

---

## 2. Prerequisites

| Requirement        | Version                |
|--------------------|------------------------|
| Java JDK           | 21+                    |
| Maven              | 3.9+ (or use wrapper)  |
| GemFire/Geode      | Optional — REST-enabled |

---

## 3. Setup Instructions

### Clone & Build

```bash
git clone <repository-url>
cd UBS
.\mvnw.cmd clean install     # Windows
./mvnw clean install          # macOS/Linux
```

### Run

```bash
.\mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run        # macOS/Linux
# OR
java -jar target/backend-service-0.0.1-SNAPSHOT.jar
```

### Verify

```bash
curl http://localhost:8080/api/v1/data/health
# → Backend service is running
```

---

## 4. Configuration

**`src/main/resources/application.yml`:**

```yaml
server:
  port: 8080
gemfire:
  base-url: http://localhost:7070
  region-name: users
  retry-attempts: 3
  retry-delay-ms: 200
```

Override via CLI: `--server.port=9090 --gemfire.base-url=http://host:7070`

Override via env: `SERVER_PORT=9090`, `GEMFIRE_BASE_URL=http://host:7070`

---

## 5. API Usage with Postman

### A. POST — Create User (JSON)

- **Method:** POST
- **URL:** `http://localhost:8080/api/v1/users`
- **Header:** `Content-Type: application/json`
- **Body (raw):**

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

- **Expected:** `201 Created`

### POST — Create User (XML)

- **Header:** `Content-Type: application/xml`
- **Body:**

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

### POST — Create User (CSV)

- **Header:** `Content-Type: text/csv`
- **Body:**

```
username,email,street,city,country
ravi,ravi@example.com,123 Main Street,Mumbai,India
```

### B. GET — Retrieve All Users

- **Method:** GET
- **URL:** `http://localhost:8080/api/v1/users`
- **Expected:** `200 OK` with JSON array of users

### C. POST — Parse Data Records

- **URL:** `http://localhost:8080/api/v1/data/parse`
- **Header:** `Content-Type: application/json`
- **Body:**

```json
{
  "format": "json",
  "payload": "[{\"id\":\"1\",\"name\":\"Alice\",\"source\":\"json\"}]"
}
```

- **Expected:** `200 OK` with `{ "format": "json", "count": 1, "records": [...] }`

---

## 6. cURL Examples

```bash
# Create User (JSON)
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"username":"ravi","email":"ravi@example.com","address":{"street":"123 Main St","city":"Mumbai","country":"India"}}'

# Create User (XML)
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/xml" \
  -d '<user><username>ravi</username><email>ravi@example.com</email><address><street>123 Main St</street><city>Mumbai</city><country>India</country></address></user>'

# Create User (CSV)
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: text/csv" \
  -d 'username,email,street,city,country
ravi,ravi@example.com,123 Main St,Mumbai,India'

# Get All Users
curl http://localhost:8080/api/v1/users

# Parse Data Records
curl -X POST http://localhost:8080/api/v1/data/parse \
  -H "Content-Type: application/json" \
  -d '{"format":"csv","payload":"id,name,source\n1,Alice,csv\n2,Bob,csv"}'

# Health Check
curl http://localhost:8080/api/v1/data/health
```

---

## 7. Troubleshooting

| Problem | Cause | Solution |
|---------|-------|----------|
| `java: error: release version 21` | Wrong Java version | Install JDK 21+, set `JAVA_HOME` |
| Port 8080 in use | Conflict | Change `server.port` |
| `400` — "Invalid JSON input…" | Malformed JSON | Validate JSON syntax |
| `400` — "Invalid XML input…" | Malformed XML | Check XML tags are closed |
| `400` — "CSV input is empty…" | No data row | Add data row after header |
| `415` — "Unsupported Content-Type." | Wrong Content-Type header | Use `application/json`, `application/xml`, or `text/csv` |
| `400` — "Username is required." | Missing `username` | Provide non-blank `username` |
| `400` — "Email is required." | Missing `email` | Provide non-blank `email` |
| `400` — "Address is required." | No `address` object | Include full address |
| `503` — "Failed to save user…" | GemFire down | Start GemFire, check `gemfire.base-url` |
| `503` after delay | Retries exhausted | Increase `gemfire.retry-attempts` |

### Running Tests

```bash
.\mvnw.cmd test                         # All tests
.\mvnw.cmd test -Dtest=UserControllerTest  # Specific test
```
