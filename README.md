# Spring Boot System Design Projects

A comprehensive collection of system design implementations using Spring Boot, demonstrating advanced architectural patterns, algorithms, and best practices.

## 📚 Projects Overview

### 1. **Spring Boot Rate Limiter** 🚦
Comprehensive rate limiting service with multiple algorithms and extensive monitoring.

**Features:**
- 5 Rate Limiting Algorithms: Token Bucket, Leaky Bucket, Fixed Window, Sliding Window Log, Sliding Window Counter
- Multiple Scoping: Global, User, IP, API Endpoint, Custom
- Annotation-based rate limiting with AOP
- Redis integration for distributed rate limiting
- Comprehensive monitoring and metrics
- Security configuration with role-based access
- Automatic cleanup and maintenance tasks

**Location:** `spring-boot-rate-limiter/`

**Quick Start:**
```bash
cd spring-boot-rate-limiter
./mvnw spring-boot:run
```

**API Examples:**
- Public endpoint: `GET /api/demo/public`
- User endpoint: `GET /api/demo/user`
- Search endpoint: `GET /api/demo/search?query=test`
- Metrics: `GET /api/rate-limit/metrics/stats`

---

### 2. **Spring Boot URL Shortener** 🔗
Complete URL shortening system with analytics, caching, and rate limiting.

**Features:**
- Custom short code generation
- Password protection for URLs
- Comprehensive analytics and tracking
- Geographic location tracking
- Rate limiting and caching with Redis
- User management and authentication
- Redirect tracking and statistics

**Location:** `spring-boot-url-shortener/`

**Quick Start:**
```bash
cd spring-boot-url-shortener
./mvnw spring-boot:run
```

**API Examples:**
- Shorten URL: `POST /api/urls/shorten`
- Redirect: `GET /{shortCode}`
- Analytics: `GET /api/analytics/{shortCode}`

---

### 3. **Spring Boot Amazon IVS Integration** 📺
Live streaming platform integration with AWS Amazon IVS.

**Features:**
- Channel management and creation
- Stream monitoring and control
- AWS IVS integration
- Real-time stream analytics
- Channel configuration and settings

**Location:** `spring-boot-amazon-ivs/`

**Quick Start:**
```bash
cd spring-boot-amazon-ivs
./mvnw spring-boot:run
```

**API Examples:**
- Create channel: `POST /api/channels`
- Get channels: `GET /api/channels`
- Stream info: `GET /api/streams/{channelId}`

---

### 4. **IVS Frontend React** ⚛️
React frontend for Amazon IVS live streaming platform.

**Features:**
- Channel management dashboard
- Live video player with IVS Player SDK
- Real-time analytics display
- Channel creation and configuration
- Stream monitoring interface

**Location:** `ivs-frontend-react/`

**Quick Start:**
```bash
cd ivs-frontend-react
npm install
npm start
```

**Pages:**
- Dashboard: `/`
- Channels: `/channels`
- Create Channel: `/channels/create`
- Live Streams: `/streams`
- Stream Player: `/streams/{id}`

---

### 5. **Spring Boot Stripe Payment** 💳
Complete payment processing system with Stripe integration.

**Features:**
- Payment processing and management
- Customer management
- Subscription handling
- Refund processing
- Payment history and tracking
- Security configuration

**Location:** `spring-boot-stripe-payment/`

**Quick Start:**
```bash
cd spring-boot-stripe-payment
./mvnw spring-boot:run
```

**API Examples:**
- Create payment: `POST /api/payments`
- Create subscription: `POST /api/subscriptions`
- Process refund: `POST /api/refunds`

---

### 6. **Spring Boot Redis Demo** 🔴
Redis integration showcase with LRU cache management.

**Features:**
- Redis operations (strings, hashes, lists, sets)
- Custom LRU cache implementation using sorted sets
- User management with caching
- Cache analytics and monitoring
- Complete API endpoints for testing

**Location:** `spring-boot-redis-demo/`

**Quick Start:**
```bash
cd spring-boot-redis-demo
./mvnw spring-boot:run
```

**API Examples:**
- Cache operations: `GET /api/cache/*`
- LRU cache: `GET /api/lru-cache/*`
- User management: `GET /api/users`

---

## 🏗️ Project Structure

```
spring-boot-projects/
├── spring-boot-rate-limiter/          # Rate limiting service
│   ├── src/main/java/com/example/ratelimiter/
│   │   ├── annotation/                # @RateLimit annotation
│   │   ├── aspect/                    # AOP for rate limiting
│   │   ├── config/                    # Redis, Security, Scheduling
│   │   ├── controller/                # REST APIs
│   │   ├── entity/                    # JPA entities
│   │   ├── enums/                     # Algorithm and scope enums
│   │   ├── repository/                # Data access layer
│   │   ├── service/                   # Business logic
│   │   │   └── algorithm/             # Rate limiting algorithms
│   │   └── util/                      # Utility classes
│   ├── demo-scripts/                  # Testing scripts
│   └── README.md
│
├── spring-boot-url-shortener/         # URL shortening service
│   ├── src/main/java/com/example/urlshortener/
│   │   ├── config/                    # Redis, Security, Scheduling
│   │   ├── controller/                # REST APIs
│   │   ├── entity/                    # JPA entities
│   │   ├── repository/                # Data access layer
│   │   ├── service/                   # Business logic
│   │   └── util/                      # Utility classes
│   └── README.md
│
├── spring-boot-amazon-ivs/            # AWS IVS integration
│   ├── src/main/java/com/example/ivs/
│   │   ├── config/                    # AWS configuration
│   │   ├── controller/                # REST APIs
│   │   ├── entity/                    # JPA entities
│   │   ├── repository/                # Data access layer
│   │   └── service/                   # Business logic
│   └── README.md
│
├── ivs-frontend-react/                # React frontend
│   ├── src/
│   │   ├── components/                # React components
│   │   ├── pages/                     # Page components
│   │   ├── services/                  # API services
│   │   └── App.js
│   └── README.md
│
├── spring-boot-stripe-payment/        # Stripe payment service
│   ├── src/main/java/com/example/stripe/
│   │   ├── config/                    # Stripe configuration
│   │   ├── controller/                # REST APIs
│   │   ├── entity/                    # JPA entities
│   │   ├── repository/                # Data access layer
│   │   └── service/                   # Business logic
│   └── README.md
│
├── spring-boot-redis-demo/            # Redis integration demo
│   ├── src/main/java/com/example/redis/
│   │   ├── config/                    # Redis configuration
│   │   ├── controller/                # REST APIs
│   │   ├── entity/                    # JPA entities
│   │   ├── repository/                # Data access layer
│   │   └── service/                   # Business logic
│   ├── demo-scripts/                  # Testing scripts
│   └── README.md
│
└── README.md                          # This file
```

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- Node.js 14+ (for React frontend)
- Redis (optional, for distributed caching)
- Docker (optional, for containerization)

### Clone the Repository
```bash
git clone https://github.com/Codes-tutorials/spring-boot-demo-projects-2.git
cd spring-boot-projects
```

### Run Individual Projects

Each project can be run independently:

```bash
# Rate Limiter
cd spring-boot-rate-limiter
./mvnw spring-boot:run

# URL Shortener
cd spring-boot-url-shortener
./mvnw spring-boot:run

# Amazon IVS Backend
cd spring-boot-amazon-ivs
./mvnw spring-boot:run

# React Frontend
cd ivs-frontend-react
npm install
npm start

# Stripe Payment
cd spring-boot-stripe-payment
./mvnw spring-boot:run

# Redis Demo
cd spring-boot-redis-demo
./mvnw spring-boot:run
```

## 🔧 Configuration

### Common Configuration Files
- `application.yml` - Spring Boot configuration
- `pom.xml` - Maven dependencies
- `package.json` - Node.js dependencies (React)

### Environment Variables
Each project may require specific environment variables. Check individual README files for details.

### Database Setup
Most projects use H2 in-memory database by default. For production, configure PostgreSQL or MySQL in `application.yml`.

### Redis Setup
For projects using Redis:
```bash
# Using Docker
docker run -d -p 6379:6379 redis:latest

# Or install locally
# macOS: brew install redis
# Linux: sudo apt-get install redis-server
# Windows: Download from https://github.com/microsoftarchive/redis/releases
```

## 📊 System Design Concepts Demonstrated

### Rate Limiting
- Token Bucket Algorithm
- Leaky Bucket Algorithm
- Fixed Window Counter
- Sliding Window Log
- Sliding Window Counter

### Caching Strategies
- LRU (Least Recently Used) Cache
- Redis Caching
- Cache Invalidation
- Distributed Caching

### Database Design
- Entity Relationships
- Indexing Strategies
- Query Optimization
- Transaction Management

### API Design
- RESTful API Principles
- Request/Response Handling
- Error Handling
- Authentication & Authorization

### Monitoring & Analytics
- Metrics Collection
- Performance Monitoring
- Health Checks
- Logging Strategies

## 🧪 Testing

### Unit Tests
```bash
cd <project-directory>
./mvnw test
```

### Integration Tests
```bash
cd <project-directory>
./mvnw verify
```

### Demo Scripts
Each project includes demo scripts for manual testing:

```bash
# Rate Limiter Demo
cd spring-boot-rate-limiter/demo-scripts
./test-rate-limiter.sh          # Linux/Mac
test-rate-limiter.bat           # Windows

# Redis Demo
cd spring-boot-redis-demo/demo-scripts
./test-redis-operations.sh      # Linux/Mac
test-redis-operations.bat       # Windows
```

## 📖 API Documentation

### Swagger/OpenAPI
Most projects include Swagger documentation accessible at:
- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

### Postman Collections
Import the provided Postman collections for easy API testing.

## 🔐 Security

### Authentication
- Basic Authentication (default)
- JWT Token Support
- Role-Based Access Control (RBAC)

### Default Credentials
- **User**: `user` / `password` (USER role)
- **Admin**: `admin` / `admin` (ADMIN role)

### CORS Configuration
CORS is configured to allow requests from:
- `http://localhost:3000` (React frontend)
- `http://localhost:3001`

## 📈 Performance Optimization

### Caching
- Redis for distributed caching
- Spring Cache abstraction
- Cache warming strategies

### Database Optimization
- Query optimization
- Index creation
- Connection pooling

### Monitoring
- Prometheus metrics
- Health checks
- Performance monitoring

## 🐳 Docker Support

Each project can be containerized:

```bash
# Build Docker image
docker build -t spring-boot-rate-limiter .

# Run container
docker run -p 8080:8080 spring-boot-rate-limiter
```

## 📝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Support

For questions and support:
- Create an issue in the repository
- Check individual project README files
- Review the documentation
- Check test cases for usage examples

## 🎯 Learning Resources

### System Design
- [System Design Interview](https://www.educative.io/courses/grokking-the-system-design-interview)
- [Designing Data-Intensive Applications](https://dataintensive.net/)

### Spring Boot
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Framework Guide](https://spring.io/guides)

### Redis
- [Redis Documentation](https://redis.io/documentation)
- [Redis Best Practices](https://redis.io/topics/best-practices)

### React
- [React Documentation](https://react.dev)
- [React Best Practices](https://react.dev/learn)

## 📞 Contact

For more information, visit:
- GitHub: https://github.com/Codes-tutorials
- Website: https://codes-tutorials.com

---

**Last Updated:** January 2026
**Version:** 1.0.0
**Status:** Production Ready ✅