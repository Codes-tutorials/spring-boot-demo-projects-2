# Spring Boot URL Shortener

A comprehensive URL shortener service built with Spring Boot, featuring analytics, caching, rate limiting, and password protection.

## Features

### Core Functionality
- **URL Shortening**: Convert long URLs into short, shareable links
- **Custom Aliases**: Create custom short codes for branded links
- **Password Protection**: Secure URLs with password authentication
- **Expiration Management**: Set custom expiration dates for URLs
- **Bulk Operations**: Handle multiple URL operations efficiently

### Analytics & Monitoring
- **Detailed Analytics**: Track clicks, unique visitors, geographic data
- **Real-time Statistics**: Monitor URL performance in real-time
- **Geographic Tracking**: Country and city-level visitor analytics
- **Device Analytics**: Browser, OS, and device type tracking
- **Referrer Tracking**: Monitor traffic sources and referrers
- **Time-based Analytics**: Hourly and daily click patterns

### Performance & Security
- **Redis Caching**: High-performance caching for fast redirects
- **Rate Limiting**: Prevent abuse with configurable rate limits
- **Security**: Password hashing, CORS protection, input validation
- **Scalability**: Designed for high-traffic scenarios
- **Monitoring**: Health checks and metrics via Spring Actuator

### Advanced Features
- **User Management**: Multi-user support with authentication
- **Admin Dashboard**: Administrative controls and system monitoring
- **Scheduled Cleanup**: Automatic cleanup of expired URLs and old analytics
- **GeoIP Integration**: Optional geographic location tracking
- **RESTful API**: Complete REST API for integration

## Technology Stack

- **Spring Boot 3.2.1**: Main framework
- **Spring Data JPA**: Database operations
- **Spring Security**: Authentication and authorization
- **Spring Cache**: Caching abstraction
- **Redis**: High-performance caching and session storage
- **H2/PostgreSQL**: Database support
- **Bucket4j**: Rate limiting
- **GeoIP2**: Geographic location services
- **UserAgentUtils**: User agent parsing
- **Maven**: Build and dependency management

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Redis server
- Optional: PostgreSQL for production

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd spring-boot-url-shortener
   ```

2. **Start Redis**
   ```bash
   # Using Docker
   docker run -d -p 6379:6379 redis:latest
   
   # Or install locally
   redis-server
   ```

3. **Configure application**
   Update `src/main/resources/application.yml`:
   ```yaml
   url-shortener:
     base-url: http://localhost:8080
   
   spring:
     redis:
       host: localhost
       port: 6379
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - API: http://localhost:8080/api
   - H2 Console: http://localhost:8080/h2-console
   - Health Check: http://localhost:8080/actuator/health

## API Documentation

### URL Shortening

#### Shorten URL
```http
POST /api/urls/shorten
Content-Type: application/json

{
    "url": "https://example.com/very/long/url",
    "customAlias": "mylink",
    "title": "My Link",
    "description": "Description of the link",
    "expiresAt": "2024-12-31T23:59:59",
    "password": "secret123",
    "analyticsEnabled": true
}
```

**Response:**
```json
{
    "id": 1,
    "shortCode": "mylink",
    "shortUrl": "http://localhost:8080/mylink",
    "originalUrl": "https://example.com/very/long/url",
    "title": "My Link",
    "description": "Description of the link",
    "createdAt": "2024-01-01T10:00:00",
    "expiresAt": "2024-12-31T23:59:59",
    "clickCount": 0,
    "isCustom": true,
    "isPasswordProtected": true,
    "analyticsEnabled": true
}
```

#### Access Short URL
```http
GET /{shortCode}
```
Redirects to the original URL or password page if protected.

#### Get User URLs
```http
GET /api/urls/my-urls?page=0&size=10
Authorization: Bearer <token>
```

#### Get URL Details
```http
GET /api/urls/{id}
Authorization: Bearer <token>
```

#### Update URL
```http
PUT /api/urls/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
    "title": "Updated Title",
    "description": "Updated Description",
    "expiresAt": "2025-12-31T23:59:59"
}
```

#### Delete URL
```http
DELETE /api/urls/{shortCode}
Authorization: Bearer <token>
```

### Analytics

#### Get URL Analytics
```http
GET /api/analytics/url/{shortCode}
Authorization: Bearer <token>
```

**Response:**
```json
{
    "urlId": 1,
    "shortCode": "mylink",
    "originalUrl": "https://example.com/very/long/url",
    "totalClicks": 150,
    "uniqueVisitors": 75,
    "createdAt": "2024-01-01T10:00:00",
    "lastAccessedAt": "2024-01-15T14:30:00",
    "dailyStats": [
        {"date": "2024-01-15", "clicks": 25},
        {"date": "2024-01-14", "clicks": 30}
    ],
    "countryStats": [
        {"country": "United States", "clicks": 50},
        {"country": "United Kingdom", "clicks": 25}
    ],
    "browserStats": [
        {"browser": "Chrome", "clicks": 75},
        {"browser": "Firefox", "clicks": 25}
    ]
}
```

### Public Endpoints

#### Get Popular URLs
```http
GET /api/urls/popular?page=0&size=10
```

#### Get Recent URLs
```http
GET /api/urls/recent?page=0&size=10
```

#### Get System Statistics
```http
GET /api/urls/stats
```

## Configuration

### Basic Configuration
```yaml
url-shortener:
  base-url: http://localhost:8080
  short-code:
    length: 7
    characters: "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  expiration:
    default-days: 365
    max-days: 3650
  analytics:
    enabled: true
  rate-limiting:
    enabled: true
    requests-per-minute: 100
    requests-per-hour: 1000
```

### Database Configuration
```yaml
# H2 (Development)
spring:
  datasource:
    url: jdbc:h2:mem:urlshortener
    driver-class-name: org.h2.Driver
    username: sa
    password: password

# PostgreSQL (Production)
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/urlshortener
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: password
```

### Redis Configuration
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    jedis:
      pool:
        max-active: 8
        max-idle: 8
```

### GeoIP Configuration
```yaml
geoip:
  enabled: true
  database-path: src/main/resources/GeoLite2-City.mmdb
```

## Usage Examples

### Basic URL Shortening
```bash
# Shorten a URL
curl -X POST http://localhost:8080/api/urls/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://example.com/very/long/url"}'

# Access the short URL
curl -L http://localhost:8080/abc123
```

### Custom Alias
```bash
curl -X POST http://localhost:8080/api/urls/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com",
    "customAlias": "example",
    "title": "Example Website"
  }'
```

### Password Protected URL
```bash
curl -X POST http://localhost:8080/api/urls/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://secret.example.com",
    "password": "secret123",
    "title": "Secret Link"
  }'
```

### Get Analytics
```bash
curl -X GET http://localhost:8080/api/analytics/url/abc123 \
  -H "Authorization: Bearer <your-token>"
```

## Rate Limiting

The service implements comprehensive rate limiting:

- **URL Creation**: 100 requests per minute, 1000 per hour
- **URL Access**: High limits for redirects
- **Analytics**: Moderate limits for dashboard access
- **Premium Users**: Higher limits for paid accounts

Rate limits are enforced per IP address for anonymous users and per user for authenticated requests.

## Security Features

### Input Validation
- URL format validation
- Custom alias character restrictions
- Length limits on all inputs
- SQL injection prevention

### Password Protection
- BCrypt password hashing
- Secure password verification
- Password-protected URL access flow

### CORS Protection
- Configurable allowed origins
- Secure headers
- Credential support

### Rate Limiting
- Distributed rate limiting with Redis
- Multiple rate limit tiers
- Automatic bucket cleanup

## Monitoring & Observability

### Health Checks
```bash
curl http://localhost:8080/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

### Custom Metrics
- Total URLs created
- Total clicks recorded
- Active URLs count
- Cache hit/miss ratios
- Rate limit violations

## Deployment

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/spring-boot-url-shortener-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_REDIS_HOST=redis
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/urlshortener
    depends_on:
      - redis
      - postgres
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  
  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=urlshortener
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    ports:
      - "5432:5432"
```

### Production Configuration
```yaml
# Production settings
spring:
  profiles:
    active: production
  
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  
  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT}
    password: ${REDIS_PASSWORD}

url-shortener:
  base-url: ${BASE_URL}
  
security:
  jwt:
    secret: ${JWT_SECRET}

geoip:
  enabled: true
  database-path: ${GEOIP_DATABASE_PATH}
```

## Performance Optimization

### Caching Strategy
- Redis caching for URL lookups
- Configurable TTL values
- Cache warming for popular URLs
- Automatic cache invalidation

### Database Optimization
- Proper indexing on frequently queried columns
- Connection pooling
- Query optimization
- Batch operations for analytics

### Rate Limiting
- Distributed rate limiting with Redis
- Efficient bucket algorithms
- Automatic cleanup of expired buckets

## Troubleshooting

### Common Issues

#### Redis Connection Error
```
Unable to connect to Redis
```
**Solution**: Ensure Redis is running and accessible
```bash
redis-cli ping
```

#### Database Connection Error
```
Connection refused to database
```
**Solution**: Check database configuration and connectivity

#### Rate Limit Exceeded
```
HTTP 429 - Rate limit exceeded
```
**Solution**: Implement exponential backoff or increase limits

#### GeoIP Database Missing
```
GeoIP database not found
```
**Solution**: Download GeoLite2 database or disable GeoIP

### Debug Mode
Enable debug logging:
```yaml
logging:
  level:
    com.example.urlshortener: DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review the API documentation
3. Create a GitHub issue
4. Contact the development team