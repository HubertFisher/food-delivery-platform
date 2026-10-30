# Food Delivery Platform

Real-time food delivery microservices platform (inspired by UberEats / DoorDash), built with **Java 21**, **Spring Boot 3**, **Spring Cloud**, **Apache Kafka**, **RabbitMQ**, and **gRPC**.

---

## 🛠 Tech Stack

| Category | Technologies |
|---|---|
| **Backend & Runtime** | Java 21, Spring Boot 3.3.x, Spring Cloud, Hibernate / Spring Data JPA, Maven |
| **Databases & Caching** | PostgreSQL, MongoDB, Redis (Geo-radius, Pub/Sub, Cache) |
| **Messaging & RPC** | Apache Kafka (courier GPS telemetry streaming, events), RabbitMQ (business workflows, notifications, DLQ), gRPC (high-throughput internal synchronous RPC), REST API, WebSockets |
| **Security & Auth** | Keycloak, OAuth2, OpenID Connect, JWT |
| **DevOps & Infrastructure** | Docker, Docker Compose, Kubernetes, GitHub Actions |

---

## 🏛 Architecture Overview

```
                                      +--------------------+
                                      |    API Gateway     |
                                      | (Spring Cloud GW)  |
                                      +---------+----------+
                                                |
               +-------------------+------------+-----------+--------------------+
               |                   |                        |                    |
               v                   v                        v                    v
      +-----------------+  +---------------+        +---------------+   +-----------------+
      | Catalog Service |  | Order Service |<--gRPC-| Pay Service   |   | Courier Service |
      |    (MongoDB)    |  | (PostgreSQL)  |        | (PostgreSQL)  |   |  (Redis + Kafka)|
      +-----------------+  +-------+-------+        +---------------+   +--------+--------+
                                   |                                             |
                                   | (Events)                                    | (Geo Stream)
                                   v                                             v
                          +-----------------+                           +-----------------+
                          |    RabbitMQ     |                           |  Apache Kafka   |
                          | (Order status / |                           |  (GPS tracking, |
                          |  Notifications) |                           |   telemetry)    |
                          +--------+--------+                           +-----------------+
                                   |
                                   v
                        +----------------------+
                        | Notification Service |
                        +----------------------+
```

### Microservices Breakdown
- **`api-gateway`** — Single entry point, routing, rate limiting, and Keycloak JWT validation.
- **`catalog-service`** — Restaurants, menus, dish categories, and inventory items (MongoDB for flexible schema and high-performance querying).
- **`order-service`** — Order lifecycle management, checkout, pricing calculation, and Saga orchestration (PostgreSQL + Transactional Outbox pattern).
- **`payment-service`** — Payment processing and third-party payment gateway integration (PostgreSQL).
- **`delivery-service`** / **`courier-service`** — Real-time courier dispatch and GPS geolocation tracking (Redis Geo-spatial + Apache Kafka).
- **`notification-service`** — Customer and restaurant notifications via Email/SMS/Push (RabbitMQ consumer).
- **`common-grpc`** — Shared Protobuf schemas, gRPC service stubs, and common DTOs.

---

## 🚀 Getting Started

### Prerequisites
- **JDK 21**
- **Docker** and **Docker Compose**

### Build the Project
```bash
./mvnw clean install
```
