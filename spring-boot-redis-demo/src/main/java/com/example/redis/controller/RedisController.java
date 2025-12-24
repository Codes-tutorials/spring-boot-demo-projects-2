package com.example.redis.controller;

import com.example.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    // String Operations
    @PostMapping("/string/{key}")
    public ResponseEntity<String> setValue(@PathVariable String key, @RequestBody Object value) {
        redisService.setValue(key, value);
        return ResponseEntity.ok("Value set successfully");
    }

    @PostMapping("/string/{key}/ttl/{seconds}")
    public ResponseEntity<String> setValueWithTTL(@PathVariable String key, 
                                                 @PathVariable long seconds, 
                                                 @RequestBody Object value) {
        redisService.setValue(key, value, seconds, TimeUnit.SECONDS);
        return ResponseEntity.ok("Value set with TTL successfully");
    }

    @GetMapping("/string/{key}")
    public ResponseEntity<Object> getValue(@PathVariable String key) {
        Object value = redisService.getValue(key);
        if (value != null) {
            return ResponseEntity.ok(value);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/string/{key}")
    public ResponseEntity<String> deleteKey(@PathVariable String key) {
        Boolean deleted = redisService.deleteKey(key);
        if (deleted) {
            return ResponseEntity.ok("Key deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/string/{key}/exists")
    public ResponseEntity<Boolean> hasKey(@PathVariable String key) {
        Boolean exists = redisService.hasKey(key);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/string/{key}/expire/{seconds}")
    public ResponseEntity<String> setExpire(@PathVariable String key, @PathVariable long seconds) {
        Boolean result = redisService.expire(key, seconds, TimeUnit.SECONDS);
        if (result) {
            return ResponseEntity.ok("Expiration set successfully");
        }
        return ResponseEntity.badRequest().body("Failed to set expiration");
    }

    @GetMapping("/string/{key}/ttl")
    public ResponseEntity<Long> getTTL(@PathVariable String key) {
        Long ttl = redisService.getExpire(key);
        return ResponseEntity.ok(ttl);
    }

    // Hash Operations
    @PostMapping("/hash/{key}/{hashKey}")
    public ResponseEntity<String> setHashValue(@PathVariable String key, 
                                              @PathVariable String hashKey, 
                                              @RequestBody Object value) {
        redisService.setHashValue(key, hashKey, value);
        return ResponseEntity.ok("Hash value set successfully");
    }

    @GetMapping("/hash/{key}/{hashKey}")
    public ResponseEntity<Object> getHashValue(@PathVariable String key, @PathVariable String hashKey) {
        Object value = redisService.getHashValue(key, hashKey);
        if (value != null) {
            return ResponseEntity.ok(value);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/hash/{key}")
    public ResponseEntity<Map<Object, Object>> getHashEntries(@PathVariable String key) {
        Map<Object, Object> entries = redisService.getHashEntries(key);
        return ResponseEntity.ok(entries);
    }

    // List Operations
    @PostMapping("/list/{key}")
    public ResponseEntity<String> pushToList(@PathVariable String key, @RequestBody Object value) {
        Long size = redisService.pushToList(key, value);
        return ResponseEntity.ok("Value pushed to list. New size: " + size);
    }

    @DeleteMapping("/list/{key}/pop")
    public ResponseEntity<Object> popFromList(@PathVariable String key) {
        Object value = redisService.popFromList(key);
        if (value != null) {
            return ResponseEntity.ok(value);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/list/{key}")
    public ResponseEntity<List<Object>> getListRange(@PathVariable String key,
                                                    @RequestParam(defaultValue = "0") long start,
                                                    @RequestParam(defaultValue = "-1") long end) {
        List<Object> values = redisService.getListRange(key, start, end);
        return ResponseEntity.ok(values);
    }

    // Set Operations
    @PostMapping("/set/{key}")
    public ResponseEntity<String> addToSet(@PathVariable String key, @RequestBody Object[] values) {
        Long added = redisService.addToSet(key, values);
        return ResponseEntity.ok("Added " + added + " values to set");
    }

    @GetMapping("/set/{key}")
    public ResponseEntity<Set<Object>> getSetMembers(@PathVariable String key) {
        Set<Object> members = redisService.getSetMembers(key);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/set/{key}/member")
    public ResponseEntity<Boolean> isSetMember(@PathVariable String key, @RequestParam Object value) {
        Boolean isMember = redisService.isSetMember(key, value);
        return ResponseEntity.ok(isMember);
    }

    // Utility Operations
    @GetMapping("/keys")
    public ResponseEntity<Set<String>> getKeys(@RequestParam(defaultValue = "*") String pattern) {
        Set<String> keys = redisService.getKeys(pattern);
        return ResponseEntity.ok(keys);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getRedisInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("dbSize", redisService.getDbSize());
        info.put("allKeys", redisService.getKeys("*"));
        return ResponseEntity.ok(info);
    }

    @DeleteMapping("/flush")
    public ResponseEntity<String> flushAll() {
        redisService.flushAll();
        return ResponseEntity.ok("All Redis data flushed");
    }
}