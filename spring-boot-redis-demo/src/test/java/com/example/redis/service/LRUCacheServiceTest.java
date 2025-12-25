package com.example.redis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class LRUCacheServiceTest {

    @Autowired
    private LRUCacheService lruCacheService;

    @BeforeEach
    void setUp() {
        lruCacheService.clearAll();
    }

    @Test
    void testPutAndGet() {
        // Given
        String key = "test-key";
        String value = "test-value";

        // When
        lruCacheService.put(key, value);
        Object retrievedValue = lruCacheService.get(key);

        // Then
        assertEquals(value, retrievedValue);
    }

    @Test
    void testCacheMiss() {
        // Given
        String key = "non-existent-key";

        // When
        Object value = lruCacheService.get(key);

        // Then
        assertNull(value);
    }

    @Test
    void testEviction() {
        // Given
        String key = "test-key";
        String value = "test-value";
        lruCacheService.put(key, value);

        // When
        lruCacheService.evict(key);
        Object retrievedValue = lruCacheService.get(key);

        // Then
        assertNull(retrievedValue);
    }

    @Test
    void testLRUOrdering() throws InterruptedException {
        // Given
        lruCacheService.put("key1", "value1");
        Thread.sleep(10);
        lruCacheService.put("key2", "value2");
        Thread.sleep(10);
        lruCacheService.put("key3", "value3");

        // Access key1 to make it more recently used
        lruCacheService.get("key1");

        // When
        Set<Object> lruKeys = lruCacheService.getLeastRecentlyUsedKeys(2);
        Set<Object> mruKeys = lruCacheService.getMostRecentlyUsedKeys(2);

        // Then
        assertTrue(lruKeys.contains("key2"));
        assertTrue(mruKeys.contains("key1"));
    }

    @Test
    void testLRUEvictionPolicy() {
        // Given - Add 3 items
        lruCacheService.put("key1", "value1");
        lruCacheService.put("key2", "value2");
        lruCacheService.put("key3", "value3");

        // When - Evict to max size of 2
        lruCacheService.evictLeastRecentlyUsed(2);

        // Then - Should have 2 items left
        LRUCacheService.CacheStats stats = lruCacheService.getCacheStats();
        assertEquals(2, stats.getTotalKeys());
    }

    @Test
    void testCacheStats() {
        // Given
        lruCacheService.put("key1", "value1");
        lruCacheService.put("key2", "value2");

        // When
        LRUCacheService.CacheStats stats = lruCacheService.getCacheStats();

        // Then
        assertEquals(2, stats.getTotalKeys());
        assertTrue(stats.getKeys().contains("key1"));
        assertTrue(stats.getKeys().contains("key2"));
    }
}