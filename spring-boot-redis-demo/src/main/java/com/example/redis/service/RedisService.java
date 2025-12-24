package com.example.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // String Operations
    public void setValue(String key, Object value) {
        logger.info("Setting Redis key: {} with value: {}", key, value);
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValue(String key, Object value, long timeout, TimeUnit unit) {
        logger.info("Setting Redis key: {} with value: {} and TTL: {} {}", key, value, timeout, unit);
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object getValue(String key) {
        logger.info("Getting Redis key: {}", key);
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean deleteKey(String key) {
        logger.info("Deleting Redis key: {}", key);
        return redisTemplate.delete(key);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        logger.info("Setting expiration for key: {} to {} {}", key, timeout, unit);
        return redisTemplate.expire(key, timeout, unit);
    }

    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    // Hash Operations
    public void setHashValue(String key, String hashKey, Object value) {
        logger.info("Setting Redis hash - key: {}, hashKey: {}, value: {}", key, hashKey, value);
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object getHashValue(String key, String hashKey) {
        logger.info("Getting Redis hash - key: {}, hashKey: {}", key, hashKey);
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public Map<Object, Object> getHashEntries(String key) {
        logger.info("Getting all Redis hash entries for key: {}", key);
        return redisTemplate.opsForHash().entries(key);
    }

    public Boolean deleteHashKey(String key, String hashKey) {
        logger.info("Deleting Redis hash key - key: {}, hashKey: {}", key, hashKey);
        return redisTemplate.opsForHash().delete(key, hashKey) > 0;
    }

    // List Operations
    public Long pushToList(String key, Object value) {
        logger.info("Pushing to Redis list - key: {}, value: {}", key, value);
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public Object popFromList(String key) {
        logger.info("Popping from Redis list - key: {}", key);
        return redisTemplate.opsForList().rightPop(key);
    }

    public List<Object> getListRange(String key, long start, long end) {
        logger.info("Getting Redis list range - key: {}, start: {}, end: {}", key, start, end);
        return redisTemplate.opsForList().range(key, start, end);
    }

    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    // Set Operations
    public Long addToSet(String key, Object... values) {
        logger.info("Adding to Redis set - key: {}, values: {}", key, values);
        return redisTemplate.opsForSet().add(key, values);
    }

    public Set<Object> getSetMembers(String key) {
        logger.info("Getting Redis set members - key: {}", key);
        return redisTemplate.opsForSet().members(key);
    }

    public Boolean isSetMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Long removeFromSet(String key, Object... values) {
        logger.info("Removing from Redis set - key: {}, values: {}", key, values);
        return redisTemplate.opsForSet().remove(key, values);
    }

    // Sorted Set Operations
    public Boolean addToSortedSet(String key, Object value, double score) {
        logger.info("Adding to Redis sorted set - key: {}, value: {}, score: {}", key, value, score);
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    public Set<Object> getSortedSetRange(String key, long start, long end) {
        logger.info("Getting Redis sorted set range - key: {}, start: {}, end: {}", key, start, end);
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public Set<Object> getSortedSetReverseRange(String key, long start, long end) {
        logger.info("Getting Redis sorted set reverse range - key: {}, start: {}, end: {}", key, start, end);
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    public Long getSortedSetSize(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    public Double getSortedSetScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    // Utility Operations
    public Set<String> getKeys(String pattern) {
        logger.info("Getting Redis keys with pattern: {}", pattern);
        return redisTemplate.keys(pattern);
    }

    public void flushAll() {
        logger.warn("Flushing all Redis data");
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    public Long getDbSize() {
        return redisTemplate.getConnectionFactory().getConnection().dbSize();
    }
}