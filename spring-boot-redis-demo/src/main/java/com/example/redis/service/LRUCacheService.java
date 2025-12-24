package com.example.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class LRUCacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(LRUCacheService.class);
    private static final String LRU_CACHE_PREFIX = "lru:";
    private static final String ACCESS_TIME_PREFIX = "access_time:";
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Put a value in LRU cache with access time tracking
     */
    public void put(String key, Object value) {
        String cacheKey = LRU_CACHE_PREFIX + key;
        String accessTimeKey = ACCESS_TIME_PREFIX + key;
        
        logger.info("Putting value in LRU cache with key: {}", key);
        
        // Store the actual value
        redisTemplate.opsForValue().set(cacheKey, value, 10, TimeUnit.MINUTES);
        
        // Store access time for LRU tracking
        redisTemplate.opsForValue().set(accessTimeKey, System.currentTimeMillis(), 10, TimeUnit.MINUTES);
        
        // Add to sorted set for LRU management (score = access time)
        redisTemplate.opsForZSet().add("lru_keys", key, System.currentTimeMillis());
    }

    /**
     * Get a value from LRU cache and update access time
     */
    public Object get(String key) {
        String cacheKey = LRU_CACHE_PREFIX + key;
        String accessTimeKey = ACCESS_TIME_PREFIX + key;
        
        Object value = redisTemplate.opsForValue().get(cacheKey);
        
        if (value != null) {
            logger.info("Cache HIT for key: {}", key);
            
            // Update access time
            long currentTime = System.currentTimeMillis();
            redisTemplate.opsForValue().set(accessTimeKey, currentTime, 10, TimeUnit.MINUTES);
            redisTemplate.opsForZSet().add("lru_keys", key, currentTime);
            
            return value;
        } else {
            logger.info("Cache MISS for key: {}", key);
            return null;
        }
    }

    /**
     * Remove a specific key from LRU cache
     */
    public void evict(String key) {
        String cacheKey = LRU_CACHE_PREFIX + key;
        String accessTimeKey = ACCESS_TIME_PREFIX + key;
        
        logger.info("Evicting key from LRU cache: {}", key);
        
        redisTemplate.delete(cacheKey);
        redisTemplate.delete(accessTimeKey);
        redisTemplate.opsForZSet().remove("lru_keys", key);
    }

    /**
     * Get least recently used keys
     */
    public Set<Object> getLeastRecentlyUsedKeys(int count) {
        return redisTemplate.opsForZSet().range("lru_keys", 0, count - 1);
    }

    /**
     * Get most recently used keys
     */
    public Set<Object> getMostRecentlyUsedKeys(int count) {
        return redisTemplate.opsForZSet().reverseRange("lru_keys", 0, count - 1);
    }

    /**
     * Simulate LRU eviction when cache is full
     */
    public void evictLeastRecentlyUsed(int maxSize) {
        Long totalKeys = redisTemplate.opsForZSet().zCard("lru_keys");
        
        if (totalKeys != null && totalKeys > maxSize) {
            int keysToEvict = (int) (totalKeys - maxSize);
            logger.info("Cache size ({}) exceeds max size ({}). Evicting {} least recently used keys.", 
                       totalKeys, maxSize, keysToEvict);
            
            Set<Object> lruKeys = getLeastRecentlyUsedKeys(keysToEvict);
            
            for (Object key : lruKeys) {
                evict(key.toString());
            }
        }
    }

    /**
     * Get cache statistics
     */
    public CacheStats getCacheStats() {
        Long totalKeys = redisTemplate.opsForZSet().zCard("lru_keys");
        Set<Object> allKeys = redisTemplate.opsForZSet().range("lru_keys", 0, -1);
        
        return new CacheStats(
            totalKeys != null ? totalKeys.intValue() : 0,
            allKeys
        );
    }

    /**
     * Clear all LRU cache data
     */
    public void clearAll() {
        logger.info("Clearing all LRU cache data");
        
        Set<Object> allKeys = redisTemplate.opsForZSet().range("lru_keys", 0, -1);
        
        if (allKeys != null) {
            for (Object key : allKeys) {
                evict(key.toString());
            }
        }
        
        redisTemplate.delete("lru_keys");
    }

    public static class CacheStats {
        private final int totalKeys;
        private final Set<Object> keys;

        public CacheStats(int totalKeys, Set<Object> keys) {
            this.totalKeys = totalKeys;
            this.keys = keys;
        }

        public int getTotalKeys() { return totalKeys; }
        public Set<Object> getKeys() { return keys; }
    }
}