package com.example.redis.controller;

import com.example.redis.service.LRUCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/lru-cache")
public class LRUCacheController {

    @Autowired
    private LRUCacheService lruCacheService;

    @PostMapping("/{key}")
    public ResponseEntity<String> putValue(@PathVariable String key, @RequestBody Object value) {
        lruCacheService.put(key, value);
        
        // Simulate LRU eviction with max size of 5 for demo purposes
        lruCacheService.evictLeastRecentlyUsed(5);
        
        return ResponseEntity.ok("Value cached successfully with key: " + key);
    }

    @GetMapping("/{key}")
    public ResponseEntity<Object> getValue(@PathVariable String key) {
        Object value = lruCacheService.get(key);
        if (value != null) {
            return ResponseEntity.ok(value);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> evictValue(@PathVariable String key) {
        lruCacheService.evict(key);
        return ResponseEntity.ok("Key evicted successfully: " + key);
    }

    @GetMapping("/lru/{count}")
    public ResponseEntity<Set<Object>> getLeastRecentlyUsed(@PathVariable int count) {
        Set<Object> lruKeys = lruCacheService.getLeastRecentlyUsedKeys(count);
        return ResponseEntity.ok(lruKeys);
    }

    @GetMapping("/mru/{count}")
    public ResponseEntity<Set<Object>> getMostRecentlyUsed(@PathVariable int count) {
        Set<Object> mruKeys = lruCacheService.getMostRecentlyUsedKeys(count);
        return ResponseEntity.ok(mruKeys);
    }

    @PostMapping("/evict-lru/{maxSize}")
    public ResponseEntity<String> evictLeastRecentlyUsed(@PathVariable int maxSize) {
        lruCacheService.evictLeastRecentlyUsed(maxSize);
        return ResponseEntity.ok("LRU eviction completed for max size: " + maxSize);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        LRUCacheService.CacheStats stats = lruCacheService.getCacheStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalKeys", stats.getTotalKeys());
        response.put("keys", stats.getKeys());
        response.put("leastRecentlyUsed", lruCacheService.getLeastRecentlyUsedKeys(3));
        response.put("mostRecentlyUsed", lruCacheService.getMostRecentlyUsedKeys(3));
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAll() {
        lruCacheService.clearAll();
        return ResponseEntity.ok("All LRU cache data cleared");
    }

    @PostMapping("/demo")
    public ResponseEntity<Map<String, Object>> runLRUDemo() {
        // Clear existing data
        lruCacheService.clearAll();
        
        // Add some demo data
        lruCacheService.put("user:1", "John Doe");
        lruCacheService.put("user:2", "Jane Smith");
        lruCacheService.put("user:3", "Bob Johnson");
        lruCacheService.put("user:4", "Alice Brown");
        lruCacheService.put("user:5", "Charlie Wilson");
        
        // Access some keys to change their order
        lruCacheService.get("user:1");
        lruCacheService.get("user:3");
        
        // Add more keys to trigger LRU eviction
        lruCacheService.put("user:6", "David Miller");
        lruCacheService.evictLeastRecentlyUsed(5);
        
        // Get final stats
        LRUCacheService.CacheStats stats = lruCacheService.getCacheStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "LRU Demo completed");
        response.put("totalKeys", stats.getTotalKeys());
        response.put("remainingKeys", stats.getKeys());
        response.put("leastRecentlyUsed", lruCacheService.getLeastRecentlyUsedKeys(3));
        response.put("mostRecentlyUsed", lruCacheService.getMostRecentlyUsedKeys(3));
        
        return ResponseEntity.ok(response);
    }
}