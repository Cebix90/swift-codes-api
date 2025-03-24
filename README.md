# SWIFT Codes API

## Project Overview

A RESTful API that parses, stores, and exposes SWIFT (BIC) code data for banks globally.  
The system ingests CSV data, stores it in a PostgreSQL database, and provides endpoints to access or manage SWIFT code records.

## 🛠️ Technologies Used

- Java 17
- Spring Boot 3.x
- PostgreSQL
- JPA (Hibernate)
- MapStruct
- OpenCSV
- Docker & Docker Compose
- JUnit 5 & Mockito
- GitHub Actions (CI/CD)

---

## ✅ Features

- Parse and import SWIFT codes from CSV.
- Automatic parsing of CSV data on startup (ParserService).
- REST API to retrieve and manage SWIFT codes.
- Support for headquarter and branch relationships.
- Containerized with Docker and Docker Compose.
- Continuous Integration with GitHub Actions.
- Unit and integration tests included.

---

## Prerequisites

- Docker & Docker Compose
- Java 17 (for local development)
- Maven

---

## 🚀 Running Locally (Docker)

### Option 1: Run Using Prebuilt Docker Images (Recommended)

1. **Pull the Latest Images**

   ```bash
   docker-compose pull
   ```

2. **Start the Containers**

   ```bash
   docker-compose up -d
   ```

   This will:
   - Start the PostgreSQL database container (`postgres`).
   - Start the API container (`swift-codes-api`) using the prebuilt image from Docker Hub: `cebix90/swift-codes-api:latest`.


3. **Access the API**

   ```
   http://localhost:8080/v1/swift-codes
   ```

---

### Option 2: Build the Docker Image Locally (Optional)

1. **Edit `docker-compose.yml` to build locally**

   Comment out the `image` line and uncomment the `build` section:

   ```yaml
   swift-codes-api:
     # image: cebix90/swift-codes-api:latest
     build:
       context: .
       dockerfile: Dockerfile
   ```

2. **Build and Start the Containers**

   ```bash
   docker-compose up -d --build
   ```

---

## 🧪 Running Tests

### 1. Run All Tests Locally (Unit + Integration Tests)

Run the full test suite locally using Maven:

```bash
./mvnw clean test
```

This will execute:
- Unit tests for `SwiftCodeServiceImpl`, `ParserService`, etc.
- Integration tests for `CountryRepository`, `SwiftCodeRepository`, and service-layer logic.

### 2. Run Tests Inside Docker (Optional)

If you've built the Docker image locally, you can run the tests inside the container:

```bash
docker run --rm cebix90/swift-codes-api:latest ./mvnw test
```

---

## 📚 REST API Endpoints

### 1. Retrieve Details of a Single SWIFT Code  
**GET** `/v1/swift-codes/{swift-code}`

#### Response Example (Headquarter SWIFT Code)
```json
{
  "address": "string",
  "bankName": "string",
  "countryISO2": "string",
  "countryName": "string",
  "isHeadquarter": true,
  "swiftCode": "string",
  "branches": [
    {
      "address": "string",
      "bankName": "string",
      "countryISO2": "string",
      "isHeadquarter": false,
      "swiftCode": "string"
    }
  ]
}
```

#### Response Example (Branch SWIFT Code)
```json
{
  "address": "string",
  "bankName": "string",
  "countryISO2": "string",
  "countryName": "string",
  "isHeadquarter": false,
  "swiftCode": "string"
}
```

---

### 2. Retrieve All SWIFT Codes for a Specific Country  
**GET** `/v1/swift-codes/country/{countryISO2}`

#### Response Example
```json
{
  "countryISO2": "string",
  "countryName": "string",
  "swiftCodes": [
    {
      "address": "string",
      "bankName": "string",
      "countryISO2": "string",
      "isHeadquarter": true,
      "swiftCode": "string"
    },
    {
      "address": "string",
      "bankName": "string",
      "countryISO2": "string",
      "isHeadquarter": false,
      "swiftCode": "string"
    }
  ]
}
```

---

### 3. Add a New SWIFT Code  
**POST** `/v1/swift-codes`

#### Request Example
```json
{
  "address": "string",
  "bankName": "string",
  "countryISO2": "string",
  "countryName": "string",
  "isHeadquarter": true,
  "swiftCode": "string"
}
```

#### Response Example
```json
{
  "message": "SwiftCode {swiftCode} successfully created"
}
```

---

### 4. Delete a SWIFT Code  
**DELETE** `/v1/swift-codes/{swift-code}`

#### Response Example
```json
{
  "message": "SwiftCode {swiftCode} successfully deleted"
}
```

---

## 🚀 Continuous Integration

This project uses GitHub Actions for CI/CD.  
On every push to `master`, the pipeline:
- Runs unit and integration tests.
- Builds and pushes the Docker image to Docker Hub (`cebix90/swift-codes-api:latest`).

---

## ⚙️ Environment Variables (Docker)

| Variable                     | Description                         | Default                                        |
|------------------------------|-------------------------------------|------------------------------------------------|
| `SPRING_PROFILES_ACTIVE`     | Spring Boot active profile         | `docker`                                       |
| `SPRING_DATASOURCE_URL`      | JDBC URL for PostgreSQL connection | `jdbc:postgresql://db:5432/swiftcodes`        |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL username                | `user`                                         |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL password                | `password`                                     |

---
