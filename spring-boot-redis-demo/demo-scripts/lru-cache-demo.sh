#!/bin/bash

# LRU Cache Demo Script
# Demonstrates LRU cache behavior with Redis

BASE_URL="http://localhost:8080"

echo "=== LRU Cache Demonstration ==="
echo

# Clear existing cache
echo "1. Clearing existing LRU cache..."
curl -s -X DELETE $BASE_URL/api/lru-cache/clear
echo "Cache cleared."
echo

# Add initial items
echo "2. Adding initial items to cache..."
curl -X POST $BASE_URL/api/lru-cache/product:1 \
  -H "Content-Type: application/json" \
  -d '{"id": 1, "name": "Laptop", "price": 999.99}' > /dev/null

curl -X POST $BASE_URL/api/lru-cache/product:2 \
  -H "Content-Type: application/json" \
  -d '{"id": 2, "name": "Mouse", "price": 29.99}' > /dev/null

curl -X POST $BASE_URL/api/lru-cache/product:3 \
  -H "Content-Type: application/json" \
  -d '{"id": 3, "name": "Keyboard", "price": 79.99}' > /dev/null

curl -X POST $BASE_URL/api/lru-cache/product:4 \
  -H "Content-Type: application/json" \
  -d '{"id": 4, "name": "Monitor", "price": 299.99}' > /dev/null

echo "Added 4 products to cache."
echo

# Show current state
echo "3. Current cache state:"
curl -s $BASE_URL/api/lru-cache/stats | jq '{totalKeys: .totalKeys, keys: .keys}'
echo

# Access some items to change their order
echo "4. Accessing product:1 and product:3 to update their access time..."
curl -s $BASE_URL/api/lru-cache/product:1 > /dev/null
sleep 1
curl -s $BASE_URL/api/lru-cache/product:3 > /dev/null
echo "Accessed product:1 and product:3"
echo

# Show LRU order
echo "5. Current LRU order:"
echo "Least Recently Used (should show product:2, product:4):"
curl -s $BASE_URL/api/lru-cache/lru/4 | jq .
echo
echo "Most Recently Used (should show product:3, product:1):"
curl -s $BASE_URL/api/lru-cache/mru/4 | jq .
echo

# Add more items to trigger eviction
echo "6. Adding more items to trigger LRU eviction..."
curl -X POST $BASE_URL/api/lru-cache/product:5 \
  -H "Content-Type: application/json" \
  -d '{"id": 5, "name": "Webcam", "price": 89.99}' > /dev/null

curl -X POST $BASE_URL/api/lru-cache/product:6 \
  -H "Content-Type: application/json" \
  -d '{"id": 6, "name": "Headphones", "price": 149.99}' > /dev/null

echo "Added product:5 and product:6"
echo

# Force eviction to max size of 4
echo "7. Forcing LRU eviction (max size: 4)..."
curl -s -X POST $BASE_URL/api/lru-cache/evict-lru/4
echo "LRU eviction completed."
echo

# Show final state
echo "8. Final cache state after eviction:"
curl -s $BASE_URL/api/lru-cache/stats | jq '{totalKeys: .totalKeys, keys: .keys}'
echo

echo "9. Verifying evicted items are no longer accessible:"
echo "Trying to access product:2 (should be evicted):"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/api/lru-cache/product:2)
if [ $HTTP_CODE -eq 404 ]; then
    echo "✓ product:2 not found (correctly evicted)"
else
    echo "✗ product:2 still exists (unexpected)"
fi

echo "Trying to access product:1 (should still exist):"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/api/lru-cache/product:1)
if [ $HTTP_CODE -eq 200 ]; then
    echo "✓ product:1 found (correctly preserved)"
else
    echo "✗ product:1 not found (unexpected)"
fi
echo

# Demonstrate access pattern tracking
echo "10. Demonstrating access pattern tracking..."
echo "Accessing items in specific order: product:6, product:5, product:3..."
curl -s $BASE_URL/api/lru-cache/product:6 > /dev/null
sleep 1
curl -s $BASE_URL/api/lru-cache/product:5 > /dev/null
sleep 1
curl -s $BASE_URL/api/lru-cache/product:3 > /dev/null

echo
echo "Final access order (Most Recently Used):"
curl -s $BASE_URL/api/lru-cache/mru/4 | jq .

echo
echo "=== LRU Cache Demo Completed ==="
echo
echo "Key Observations:"
echo "1. Items are automatically ordered by access time"
echo "2. Least recently used items are evicted when cache is full"
echo "3. Accessing an item updates its position in the LRU order"
echo "4. The cache maintains a configurable maximum size"