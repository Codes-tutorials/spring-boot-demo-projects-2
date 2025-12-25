@echo off
REM Spring Boot Redis Demo - Test Scripts for Windows
REM Make sure the application is running on localhost:8080

set BASE_URL=http://localhost:8080

echo === Spring Boot Redis Demo Test Scripts ===
echo.

REM Test 1: User Operations with Caching
echo 1. Testing User Operations with Redis Caching
echo =============================================

REM Create a new user
echo Creating a new user...
curl -X POST %BASE_URL%/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"username\": \"redis_test_user\", \"email\": \"redis.test@example.com\", \"firstName\": \"Redis\", \"lastName\": \"Test\"}"

echo.
echo Getting user by ID (first call - database hit)...
curl -s %BASE_URL%/api/users/1

echo.
echo Getting user by ID again (second call - cache hit)...
curl -s %BASE_URL%/api/users/1

echo.
echo.

REM Test 2: Redis String Operations
echo 2. Testing Redis String Operations
echo ==================================

REM Set a string value
echo Setting string value with TTL...
curl -X POST %BASE_URL%/api/redis/string/demo-key/ttl/300 ^
  -H "Content-Type: application/json" ^
  -d "\"Hello Redis from Spring Boot!\""

echo.
echo Getting string value...
curl -s %BASE_URL%/api/redis/string/demo-key

echo.
echo Checking TTL...
curl -s %BASE_URL%/api/redis/string/demo-key/ttl

echo.
echo.

REM Test 3: LRU Cache Operations
echo 3. Testing LRU Cache Operations
echo ===============================

REM Run LRU demo
echo Running LRU cache demo...
curl -s -X POST %BASE_URL%/api/lru-cache/demo

echo.
echo Getting LRU cache statistics...
curl -s %BASE_URL%/api/lru-cache/stats

echo.
echo.

REM Test 4: Cache Management
echo 4. Testing Cache Management
echo ===========================

echo Getting all cache names...
curl -s %BASE_URL%/api/cache/names

echo.
echo Getting Redis information...
curl -s %BASE_URL%/api/redis/info

echo.
echo === All tests completed! ===
pause