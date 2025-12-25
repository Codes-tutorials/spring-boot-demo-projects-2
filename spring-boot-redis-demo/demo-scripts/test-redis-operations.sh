#!/bin/bash

# Spring Boot Redis Demo - Test Scripts
# Make sure the application is running on localhost:8080

BASE_URL="http://localhost:8080"

echo "=== Spring Boot Redis Demo Test Scripts ==="
echo

# Test 1: User Operations with Caching
echo "1. Testing User Operations with Redis Caching"
echo "============================================="

# Create a new user
echo "Creating a new user..."
curl -X POST $BASE_URL/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "redis_test_user",
    "email": "redis.test@example.com",
    "firstName": "Redis",
    "lastName": "Test"
  }' | jq .

echo
echo "Getting user by ID (first call - database hit)..."
curl -s $BASE_URL/api/users/1 | jq .

echo
echo "Getting user by ID again (second call - cache hit)..."
curl -s $BASE_URL/api/users/1 | jq .

echo
echo "Getting user by username (cache hit)..."
curl -s $BASE_URL/api/users/username/redis_test_user | jq .

echo
echo

# Test 2: Redis String Operations
echo "2. Testing Redis String Operations"
echo "=================================="

# Set a string value
echo "Setting string value with TTL..."
curl -X POST $BASE_URL/api/redis/string/demo-key/ttl/300 \
  -H "Content-Type: application/json" \
  -d '"Hello Redis from Spring Boot!"'

echo
echo "Getting string value..."
curl -s $BASE_URL/api/redis/string/demo-key | jq .

echo
echo "Checking TTL..."
curl -s $BASE_URL/api/redis/string/demo-key/ttl

echo
echo

# Test 3: Redis Hash Operations
echo "3. Testing Redis Hash Operations"
echo "==============================="

# Set hash values
echo "Setting hash values..."
curl -X POST $BASE_URL/api/redis/hash/user:profile/name \
  -H "Content-Type: application/json" \
  -d '"John Doe"'

curl -X POST $BASE_URL/api/redis/hash/user:profile/age \
  -H "Content-Type: application/json" \
  -d '30'

curl -X POST $BASE_URL/api/redis/hash/user:profile/city \
  -H "Content-Type: application/json" \
  -d '"New York"'

echo
echo "Getting all hash entries..."
curl -s $BASE_URL/api/redis/hash/user:profile | jq .

echo
echo

# Test 4: Redis List Operations
echo "4. Testing Redis List Operations"
echo "==============================="

# Push to list
echo "Pushing items to list..."
curl -X POST $BASE_URL/api/redis/list/task-queue \
  -H "Content-Type: application/json" \
  -d '"Task 1: Process payment"'

curl -X POST $BASE_URL/api/redis/list/task-queue \
  -H "Content-Type: application/json" \
  -d '"Task 2: Send email"'

curl -X POST $BASE_URL/api/redis/list/task-queue \
  -H "Content-Type: application/json" \
  -d '"Task 3: Update inventory"'

echo
echo "Getting list contents..."
curl -s $BASE_URL/api/redis/list/task-queue | jq .

echo
echo

# Test 5: Redis Set Operations
echo "5. Testing Redis Set Operations"
echo "==============================="

# Add to set
echo "Adding items to set..."
curl -X POST $BASE_URL/api/redis/set/user-tags \
  -H "Content-Type: application/json" \
  -d '["developer", "java", "spring", "redis", "cache"]'

echo
echo "Getting set members..."
curl -s $BASE_URL/api/redis/set/user-tags | jq .

echo
echo "Checking membership..."
curl -s "$BASE_URL/api/redis/set/user-tags/member?value=java"

echo
echo

# Test 6: LRU Cache Operations
echo "6. Testing LRU Cache Operations"
echo "==============================="

# Run LRU demo
echo "Running LRU cache demo..."
curl -s -X POST $BASE_URL/api/lru-cache/demo | jq .

echo
echo "Adding more items to LRU cache..."
curl -X POST $BASE_URL/api/lru-cache/session:user1 \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "sessionId": "sess_123",
    "loginTime": "2024-01-01T10:00:00",
    "lastActivity": "2024-01-01T10:30:00"
  }'

curl -X POST $BASE_URL/api/lru-cache/session:user2 \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "sessionId": "sess_456",
    "loginTime": "2024-01-01T11:00:00",
    "lastActivity": "2024-01-01T11:15:00"
  }'

echo
echo "Getting LRU cache statistics..."
curl -s $BASE_URL/api/lru-cache/stats | jq .

echo
echo "Getting least recently used keys..."
curl -s $BASE_URL/api/lru-cache/lru/3 | jq .

echo
echo "Getting most recently used keys..."
curl -s $BASE_URL/api/lru-cache/mru/3 | jq .

echo
echo

# Test 7: Cache Management
echo "7. Testing Cache Management"
echo "==========================="

echo "Getting all cache names..."
curl -s $BASE_URL/api/cache/names | jq .

echo
echo "Getting cache statistics for 'users' cache..."
curl -s $BASE_URL/api/cache/users/stats | jq .

echo
echo

# Test 8: Redis Info
echo "8. Testing Redis Info"
echo "===================="

echo "Getting Redis information..."
curl -s $BASE_URL/api/redis/info | jq .

echo
echo "Getting all Redis keys..."
curl -s $BASE_URL/api/redis/keys | jq .

echo
echo "=== All tests completed! ==="