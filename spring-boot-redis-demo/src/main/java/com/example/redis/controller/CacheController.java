package com.example.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/names")
    public ResponseEntity<Collection<String>> getCacheNames() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        return ResponseEntity.ok(cacheNames);
    }

    @GetMapping("/{cacheName}/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheName", cacheName);
        stats.put("cacheType", cache.getClass().getSimpleName());
        stats.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());

        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{cacheName}/clear")
    public ResponseEntity<String> clearCache(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }

        cache.clear();
        return ResponseEntity.ok("Cache '" + cacheName + "' cleared successfully");
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<String> clearAllCaches() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }

        return ResponseEntity.ok("All caches cleared successfully");
    }

    @GetMapping("/{cacheName}/get/{key}")
    public ResponseEntity<Object> getCacheValue(@PathVariable String cacheName, @PathVariable String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }

        Cache.ValueWrapper valueWrapper = cache.get(key);
        if (valueWrapper != null) {
            return ResponseEntity.ok(valueWrapper.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{cacheName}/put/{key}")
    public ResponseEntity<String> putCacheValue(@PathVariable String cacheName, 
                                               @PathVariable String key, 
                                               @RequestBody Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }

        cache.put(key, value);
        return ResponseEntity.ok("Value cached successfully");
    }

    @DeleteMapping("/{cacheName}/evict/{key}")
    public ResponseEntity<String> evictCacheValue(@PathVariable String cacheName, @PathVariable String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }

        cache.evict(key);
        return ResponseEntity.ok("Cache key evicted successfully");
    }
}