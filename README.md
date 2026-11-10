# 🔗 Scalable URL Shortener Service

A scalable, containerized URL Shortener built using **Java 21, Spring Boot, PostgreSQL, Redis, Kafka, Docker, and Nginx**.

The application generates unique short URLs, redirects users to their original destinations, and uses Redis caching to optimize repeated URL lookups. It supports horizontal scaling through multiple Spring Boot instances behind an Nginx load balancer. Kafka enables asynchronous email notifications whenever a user creates a shortened URL.

---

## 🚀 Overview

This project explores the design and implementation of a scalable backend service, focusing on:

* REST API development using Spring Boot.
* Persistent storage using PostgreSQL.
* Cache-aside implementation using Redis.
* Containerization and service orchestration using Docker Compose.
* Horizontal application scaling using multiple replicas.
* Reverse proxy and load balancing using Nginx.
* Asynchronous event processing using Apache Kafka.
* Email notifications using Spring Boot Mail and Gmail SMTP.
* Performance benchmarking using Apache JMeter.

The objective is not just to shorten URLs, but to understand how backend services can be designed to handle increasing traffic while maintaining performance and reliability.

---

## 🏗️ Architecture

The application follows a containerized, multi-instance architecture.

**Request flow:**

1. A client sends a request to the application.
2. Nginx receives the request and distributes it across available Spring Boot instances.
3. The application processes the request.
4. PostgreSQL stores URL information and expiry metadata.
5. Redis caches frequently accessed URL mappings.
6. Redirect requests use Redis when a cached mapping is available, falling back to PostgreSQL on a cache miss.
7. After creating a shortened URL, the application publishes a `URL_CREATED` event to Kafka.
8. A Kafka consumer processes the event asynchronously and triggers the email service to send the shortened URL to the user's email address.

### Architecture Diagram

![Scalable URL Shortener System Architecture](/url-shortener-service/src/main/java/in/cper/url_shortener_service/doc/final-high-scale-url-shortener-architecture.png)

---

## 🛠️ Tech Stack

| Technology                  | Purpose                                    |
| --------------------------- | ------------------------------------------ |
| Java 21                     | Programming language                       |
| Spring Boot                 | Backend application framework              |
| Spring Data JPA / Hibernate | Database persistence                       |
| PostgreSQL                  | Persistent relational storage              |
| Redis                       | In-memory caching and rate limiting        |
| Apache Kafka                | Asynchronous event streaming               |
| Spring Kafka                | Kafka producer and consumer integration    |
| Spring Boot Mail            | Email integration using SMTP               |
| Gmail SMTP                  | Email delivery                              |
| Docker                      | Application containerization               |
| Docker Compose              | Multi-container orchestration              |
| Nginx                       | Reverse proxy and load balancing           |
| Apache JMeter               | Load and performance testing               |
| Maven                       | Dependency management and build automation |

---

## ✨ Features

### 1. URL Shortening

* Generates random, unique short codes.
* Uses `SecureRandom` for short-code generation.
* Supports configurable short-code length.
* Stores original URL and short-code mappings in PostgreSQL.

### 2. URL Redirection

* Redirects users using generated short codes.
* Returns HTTP 302 redirects.
* Validates URL expiration.
* Handles invalid and expired short codes.

### 3. Redis Caching

Implements the **Cache-Aside Pattern** to reduce repeated database lookups.

**Cache miss:**

1. Check Redis for the requested short code.
2. If unavailable, query PostgreSQL.
3. Validate URL expiration.
4. Retrieve the original URL.
5. Store the mapping in Redis with an expiry-based TTL.

**Cache hit:**

1. Retrieve the original URL directly from Redis.
2. Avoid an additional PostgreSQL lookup.

### 4. Dockerized Deployment

* Containerized Spring Boot application.
* PostgreSQL and Redis running as separate containers.
* Docker Compose manages the application infrastructure.
* Persistent Docker volumes for database and Redis data.

### 5. Horizontal Scaling

* Runs multiple Spring Boot application replicas.
* Uses Nginx as a reverse proxy and load balancer.
* Distributes incoming traffic across application instances.
* Supports continued request handling when an individual application instance is stopped, subject to the configured Nginx upstream behavior.

### 6. Redis-Based Rate Limiting

* Implements distributed rate limiting using Redis.
* Uses `OncePerRequestFilter` to intercept incoming requests.
* Tracks request counts using Redis operations.
* Applies rate limits across multiple Spring Boot replicas.
* Returns HTTP `429 Too Many Requests` when the configured request threshold is exceeded.

### 7. Asynchronous Email Notifications Using Kafka

Integrates Apache Kafka to decouple URL creation from email delivery.

**Workflow:**

1. The user submits an original URL along with a recipient email address.
2. Spring Boot generates a unique short code and saves the URL mapping in PostgreSQL.
3. The `UrlEventProducer` publishes a `URL_CREATED` event to the Kafka topic `url-events`.
4. Kafka stores the event for consumption.
5. The `UrlEventConsumer` listens to the topic using the consumer group `url-event-group`.
6. The consumer receives the event and invokes `EmailService`.
7. `JavaMailSender` sends an email containing the original URL and generated short URL through Gmail SMTP.

**Key implementation details:**

* Uses `KafkaTemplate` to publish events.
* Uses `@KafkaListener` to consume events.
* Uses a Java record (`UrlCreatedEvent`) as the event data structure.
* Uses JSON serialization and deserialization.
* Processes email notifications asynchronously.
* Uses Gmail App Password authentication for SMTP.

**Benefits:**

* Email delivery does not block the URL-shortening API response.
* Producer and consumer responsibilities remain separated.
* Kafka provides persistent event storage.
* Consumer processing can be scaled independently of the API replicas.

---

## 🔌 API Endpoints

### 1. Create Short URL

**Endpoint**

```http
POST /api/shortenURL
```

**Request Body**

```json
{
  "originalUrl": "https://www.example.com",
  "email": "recipient@example.com"
}
```

**Response**

The response contains the generated short URL and its associated mapping.

Example structure:

```json
{
  "shortCode": "Ab12Xy9",
  "originalUrl": "https://www.example.com",
  "shortUrl": "http://localhost:8080/redirect/Ab12Xy9"
}
```

*Note: Adjust the example response fields and URL path to match your actual controller implementation.*

**Email Notification:**

After successful URL creation, the application publishes a Kafka event. The consumer processes the event in the background and sends an email containing the shortened URL to the supplied email address.

### 2. Redirect to Original URL

**Endpoint**

```http
GET /api/redirectURL/{shortCode}
```

**Example**

```http
GET /api/redirectURL/Ab12Xy9
```

**Expected Behavior**

* Returns HTTP 302 when the short code is valid.
* Redirects the client to the original URL.
* Handles missing and expired short codes appropriately.

---

## 🐳 Running the Project with Docker

### Prerequisites

Install the following:

* Java 21
* Maven
* Docker Desktop
* Git

### 1. Clone the Repository

```bash
git clone <YOUR_REPOSITORY_URL>
```

```bash
cd url-shortener-service
```

### 2. Configure Environment Variables

Create a `.env` file in the project root.

Configure the environment variables required by your application:

```env
# PostgreSQL
DB_NAME=your_database
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Redis
REDIS_PASSWORD=your_redis_password

# Application
PLATFORM_URL=http://localhost:8080

# Kafka
KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Gmail SMTP
SMTP_USERNAME=yourgmail@gmail.com
SMTP_APP_PASSWORD=your_google_app_password
```

Add any additional configuration properties required by your `application.yml`.

**Important:** Never commit `.env` or expose credentials in the repository.

### 3. Start the Application

Build and start the services:

```bash
docker compose up -d --build
```

### 4. Run Multiple Application Replicas

```bash
docker compose up -d --scale app=3
```

Nginx acts as the entry point and distributes requests across the Spring Boot replicas.

### 5. Verify Running Containers

```bash
docker compose ps
```

### 6. Access the Application

```text
http://localhost:8080
```

---

## 📊 Performance Testing

Apache JMeter was used to evaluate application performance under concurrent requests.

### Test Configuration

| Parameter               | Configuration     |
| ----------------------- | ----------------- |
| Threads                 | 500               |
| Ramp-up period          | 5 seconds         |
| Loop count              | 10                |
| Total expected requests | 5,000             |
| HTTP Method             | POST              |
| Endpoint                | `/api/shortenURL` |
| Load balancing          | Nginx             |

### Observed Results

The following results are averages of three runs per configuration.

| Metric                |      1 Replica |     3 Replicas |
| --------------------- | -------------: | -------------: |
| Average response time |       1,750 ms |      626.36 ms |
| Average throughput    | 213.13 req/sec | 399.56 req/sec |
| Average error rate    |         0.387% |             0% |

### Key Observations

* Approximately **1.88× higher throughput** with three replicas.
* Approximately **64.2% lower average response time**.
* Zero reported errors across the three selected three-replica runs.
* Nginx distributed a sample of 30 requests evenly across all three application instances.
* The service continued responding after one application replica was stopped.

*Benchmark results are from local Docker-based testing. They are workload-, environment-, and run-dependent and should not be interpreted as production capacity guarantees.*

---

## 🧪 Testing

The application was tested using:

* Postman for API validation.
* Apache JMeter for concurrent load testing.
* Docker CLI for container inspection.
* Redis CLI for cache verification.
* Docker statistics for CPU and memory monitoring.
* Nginx upstream headers for load-balancing verification.
* Kafka CLI for topic creation and inspection.
* Application logs for Kafka producer and consumer verification.
* Gmail inbox verification for email delivery.

---

## 📁 Project Structure

```text
url-shortener-service/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── in/cper/url_shortener_service/
│       │       ├── controller/
│       │       ├── doc/
│       │       ├── service/
│       │       ├── repository/
│       │       ├── entity/
│       │       ├── dto/
│       │       ├── event/
│       │       ├── filter/
│       │       ├── mapper/
│       │       ├── exception/
│       │       └── utils/
│       │
│       └── resources/
│           └── application.yml
│
├── nginx/
│   └── nginx.conf
│
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── .gitignore
├── pom.xml
└── README.md
```

*The structure above is illustrative; adjust directory names to match your repository.*

---

## 🔮 Future Improvements

Potential enhancements planned for future iterations:

* Redis failure fallback to PostgreSQL.
* Automated health-based traffic management.
* Database optimization and indexing.
* Improved Kafka reliability with retry handling and dead-letter topics.
* Transactional event publishing using the Outbox Pattern.
* Distributed monitoring and centralized logging.
* Cloud deployment using AWS.
* Advanced performance testing and bottleneck analysis.
* CI/CD pipeline integration.

---

## 🎯 Learning Outcomes

Through this project, I explored practical backend engineering concepts:

* Building RESTful services with Spring Boot.
* Database persistence and entity management.
* Cache-aside architecture and TTL management.
* Redis-based distributed rate limiting.
* Docker containerization and orchestration.
* Reverse proxy configuration and load balancing.
* Horizontal scaling of stateless application instances.
* Kafka producers, consumers, topics, partitions, and consumer groups.
* Asynchronous event-driven processing.
* Email integration using Spring Boot Mail and Gmail SMTP.
* Performance benchmarking and resource monitoring.
* Basic failure-tolerance testing.

---

## 👨‍💻 Author

**Ayush Raj**

Java Backend Developer | Spring Boot | Distributed Systems

---

⭐ If you find this project interesting, consider giving it a star!
