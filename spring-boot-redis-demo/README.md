# Spring Boot Redis Integration Demo

This project demonstrates comprehensive Redis integration with Spring Boot, including caching, LRU cache management, and various Redis data structures.

## Features

### 1. Redis Configuration
- Custom Redis configuration with Jedis connection factory
- Multiple cache configurations with different TTL settings
- JSON serialization for complex objects

### 2. User Management with Caching
- JPA entities with H2 database
- Redis caching using Spring Cache annotations
- Cache eviction and update strategies

### 3. LRU Cache Implementation
- Custom LRU cache service using Redis sorted sets
- Access time tracking for proper LRU behavior
- Automatic eviction of least recently used items

### 4. Redis Data Structures
- String operations with TTL support
- Hash operations for structured data
- List operations for queues and stacks
- Set operations for unique collections
- Sorted set operations for ranked data

## Prerequisites

- Java 17+
- Maven 3.6+
- Redis server running on localhost:6379

## Getting Started

### 1. Start Redis Server
```bash
# Using Docker
docker run -d -p 6379:6379 redis:latest

# Or install Redis locally and start
redis-server
```

### 2. Run the Application
```bash
cd spring-boot-redis-demo
mvn spring-boot:run
```

### 3. Access H2 Console (Optional)
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

## API Endpoints

### User Management (with Redis Caching)

#### Get All Users
```bash
GET /api/users
```

#### Get User by ID (Cached)
```bash
GET /api/users/{id}
```

#### Get User by Username (Cached)
```bash
GET /api/users/username/{username}
```

#### Create User
```bash
POST /api/users
Content-Type: application/json

{
    "username": "new_user",
    "email": "new.user@example.com",
    "firstName": "New",
    "lastName": "User"
}
```

#### Update User (Cache Updated)
```bash
PUT /api/users/{id}
Content-Type: application/json

{
    "username": "updated_user",
    "email": "updated.user@example.com",
    "firstName": "Updated",
    "lastName": "User"
}
```

#### Delete User (Cache Evicted)
```bash
DELETE /api/users/{id}
```

### Redis Operations

#### String Operations
```bash
# Set value
POST /api/redis/string/{key}
Content-Type: application/json
"value"

# Set value with TTL
POST /api/redis/string/{key}/ttl/{seconds}
Content-Type: application/json
"value"

# Get value
GET /api/redis/string/{key}

# Delete key
DELETE /api/redis/string/{key}

# Check if key exists
GET /api/redis/string/{key}/exists

# Set expiration
POST /api/redis/string/{key}/expire/{seconds}

# Get TTL
GET /api/redis/string/{key}/ttl
```

#### Hash Operations
```bash
# Set hash value
POST /api/redis/hash/{key}/{hashKey}
Content-Type: application/json
"value"

# Get hash value
GET /api/redis/hash/{key}/{hashKey}

# Get all hash entries
GET /api/redis/hash/{key}
```

#### List Operations
```bash
# Push to list
POST /api/redis/list/{key}
Content-Type: application/json
"value"

# Pop from list
DELETE /api/redis/list/{key}/pop

# Get list range
GET /api/redis/list/{key}?start=0&end=-1
```

#### Set Operations
```bash
# Add to set
POST /api/redis/set/{key}
Content-Type: application/json
["value1", "value2", "value3"]

# Get set members
GET /api/redis/set/{key}

# Check membership
GET /api/redis/set/{key}/member?value=somevalue
```

#### Utility Operations
```bash
# Get all keys
GET /api/redis/keys?pattern=*

# Get Redis info
GET /api/redis/info

# Flush all data
DELETE /api/redis/flush
```

### LRU Cache Management

#### Cache Value
```bash
POST /api/lru-cache/{key}
Content-Type: application/json
{
    "data": "some value",
    "timestamp": "2024-01-01T00:00:00"
}
```

#### Get Cached Value
```bash
GET /api/lru-cache/{key}
```

#### Evict Key
```bash
DELETE /api/lru-cache/{key}
```

#### Get Least Recently Used Keys
```bash
GET /api/lru-cache/lru/{count}
```

#### Get Most Recently Used Keys
```bash
GET /api/lru-cache/mru/{count}
```

#### Force LRU Eviction
```bash
POST /api/lru-cache/evict-lru/{maxSize}
```

#### Get Cache Statistics
```bash
GET /api/lru-cache/stats
```

#### Run LRU Demo
```bash
POST /api/lru-cache/demo
```

#### Clear All Cache
```bash
DELETE /api/lru-cache/clear
```

## LRU Cache Implementation Details

### How It Works
1. **Storage**: Values are stored with a `lru:` prefix
2. **Access Tracking**: Access times are stored with `access_time:` prefix
3. **Ordering**: A sorted set `lru_keys` maintains keys ordered by access time
4. **Eviction**: When cache exceeds max size, least recently used keys are removed

### Key Features
- **Automatic Eviction**: Configurable maximum cache size
- **Access Time Updates**: Every get operation updates access time
- **Statistics**: Real-time cache statistics and key ordering
- **TTL Support**: All cached items have configurable TTL

## Testing the Application

### 1. Test User Caching
```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"test_user","email":"test@example.com","firstName":"Test","lastName":"User"}'

# Get user by ID (first call - database hit)
curl http://localhost:8080/api/users/1

# Get user by ID again (second call - cache hit)
curl http://localhost:8080/api/users/1
```

### 2. Test Redis Operations
```bash
# Set a value with TTL
curl -X POST http://localhost:8080/api/redis/string/test-key/ttl/60 \
  -H "Content-Type: application/json" \
  -d '"Hello Redis"'

# Get the value
curl http://localhost:8080/api/redis/string/test-key

# Check TTL
curl http://localhost:8080/api/redis/string/test-key/ttl
```

### 3. Test LRU Cache
```bash
# Run the LRU demo
curl -X POST http://localhost:8080/api/lru-cache/demo

# Check cache statistics
curl http://localhost:8080/api/lru-cache/stats

# Add more items to test eviction
curl -X POST http://localhost:8080/api/lru-cache/item1 \
  -H "Content-Type: application/json" \
  -d '{"data":"First item"}'

curl -X POST http://localhost:8080/api/lru-cache/item2 \
  -H "Content-Type: application/json" \
  -d '{"data":"Second item"}'
```

## Configuration

### Redis Configuration
- **Host**: localhost
- **Port**: 6379
- **Connection Pool**: Jedis with max 8 connections
- **Serialization**: JSON for values, String for keys

### Cache Configuration
- **Default TTL**: 10 minutes
- **User Cache TTL**: 5 minutes
- **Product Cache TTL**: 15 minutes
- **LRU Cache TTL**: 2 minutes

### LRU Settings
- **Max Memory**: 100MB
- **Memory Policy**: allkeys-lru
- **Default Cache Size**: 1000 items

## Monitoring

### Logs
The application provides detailed logging for:
- Cache hits and misses
- Database operations
- Redis operations
- LRU evictions

### Metrics
- Cache statistics via `/api/lru-cache/stats`
- Redis info via `/api/redis/info`
- Key patterns via `/api/redis/keys`

## Best Practices Demonstrated

1. **Proper Serialization**: JSON serialization for complex objects
2. **TTL Management**: Different TTL for different data types
3. **Cache Strategies**: Read-through, write-through, and write-behind patterns
4. **LRU Implementation**: Efficient LRU using Redis sorted sets
5. **Error Handling**: Graceful handling of cache misses and Redis failures
6. **Monitoring**: Comprehensive logging and statistics

## Troubleshooting

### Redis Connection Issues
- Ensure Redis server is running on localhost:6379
- Check firewall settings
- Verify Redis configuration

### Cache Not Working
- Check Redis connectivity
- Verify cache configuration in application.yml
- Enable debug logging for cache operations

### Performance Issues
- Monitor Redis memory usage
- Adjust connection pool settings
- Optimize TTL values based on usage patterns