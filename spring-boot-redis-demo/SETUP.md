# Spring Boot Redis Demo - Setup Guide

## Quick Setup Options

### Option 1: Using Docker Compose (Recommended)

1. **Start Redis with Docker Compose**
   ```bash
   cd spring-boot-redis-demo
   docker-compose up -d
   ```
   This starts:
   - Redis server on port 6379
   - Redis Commander (Web UI) on port 8081

2. **Run the Spring Boot Application**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the Application**
   - API: http://localhost:8080
   - Redis Commander: http://localhost:8081
   - H2 Console: http://localhost:8080/h2-console

### Option 2: Local Redis Installation

#### Windows
1. **Install Redis using Chocolatey**
   ```cmd
   choco install redis-64
   ```

2. **Or download from GitHub**
   - Download from: https://github.com/microsoftarchive/redis/releases
   - Extract and run `redis-server.exe`

3. **Start Redis**
   ```cmd
   redis-server
   ```

#### macOS
```bash
# Using Homebrew
brew install redis
brew services start redis
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install redis-server
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

## Running the Application

1. **Clone and Navigate**
   ```bash
   cd spring-boot-redis-demo
   ```

2. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **Verify Redis Connection**
   Check application logs for:
   ```
   INFO  - Connected to Redis successfully
   INFO  - Initializing sample data...
   ```

## Testing the Setup

### Quick Health Check
```bash
# Check if application is running
curl http://localhost:8080/api/users

# Check Redis connectivity
curl http://localhost:8080/api/redis/info
```

### Run Demo Scripts

#### Windows
```cmd
cd demo-scripts
test-redis-operations.bat
```

#### Linux/macOS
```bash
cd demo-scripts
chmod +x *.sh
./test-redis-operations.sh
./lru-cache-demo.sh
```

## Configuration

### Redis Configuration (application.yml)
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
        min-idle: 0
```

### LRU Configuration
```yaml
redis:
  lru:
    max-memory: 100mb
    max-memory-policy: allkeys-lru
    cache-size: 1000
```

## Troubleshooting

### Redis Connection Issues

1. **Check Redis is Running**
   ```bash
   redis-cli ping
   # Should return: PONG
   ```

2. **Check Port Availability**
   ```bash
   netstat -an | grep 6379
   ```

3. **Windows Firewall**
   - Allow Redis through Windows Firewall
   - Or disable firewall temporarily for testing

### Application Issues

1. **Port 8080 Already in Use**
   ```yaml
   # Add to application.yml
   server:
     port: 8081
   ```

2. **H2 Database Issues**
   - Check H2 console: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`, Password: `password`

### Performance Issues

1. **Increase Redis Memory**
   ```bash
   # In redis.conf or docker-compose.yml
   maxmemory 256mb
   ```

2. **Adjust Connection Pool**
   ```yaml
   spring:
     redis:
       jedis:
         pool:
           max-active: 16
           max-idle: 16
   ```

## Monitoring

### Redis Commander (Web UI)
- URL: http://localhost:8081
- Browse Redis keys and values
- Monitor memory usage
- Execute Redis commands

### Application Endpoints
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Cache Stats: http://localhost:8080/api/cache/names

### Redis CLI Commands
```bash
# Connect to Redis
redis-cli

# Monitor commands
MONITOR

# Check memory usage
INFO memory

# List all keys
KEYS *

# Check LRU keys
ZRANGE lru_keys 0 -1 WITHSCORES
```

## Next Steps

1. **Explore API Endpoints**
   - User Management: `/api/users`
   - Redis Operations: `/api/redis`
   - LRU Cache: `/api/lru-cache`
   - Cache Management: `/api/cache`

2. **Run Performance Tests**
   - Use demo scripts to simulate load
   - Monitor cache hit/miss ratios
   - Test LRU eviction behavior

3. **Customize Configuration**
   - Adjust TTL values
   - Modify LRU cache size
   - Configure different cache strategies

4. **Integration Testing**
   - Run unit tests: `mvn test`
   - Load testing with tools like JMeter
   - Monitor Redis performance under load