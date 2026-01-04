package com.example.urlshortener.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingService.class);
    
    @Value("${url-shortener.rate-limiting.enabled}")
    private boolean rateLimitingEnabled;
    
    @Value("${url-shortener.rate-limiting.requests-per-minute}")
    private int requestsPerMinute;
    
    @Value("${url-shortener.rate-limiting.requests-per-hour}")
    private int requestsPerHour;
    
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {
        if (!rateLimitingEnabled) {
            return true;
        }
        
        Bucket bucket = buckets.computeIfAbsent(key, this::createBucket);
        boolean allowed = bucket.tryConsume(1);
        
        if (!allowed) {
            logger.warn("Rate limit exceeded for key: {}", key);
        }
        
        return allowed;
    }

    public boolean isAllowed(String key, int tokens) {
        if (!rateLimitingEnabled) {
            return true;
        }
        
        Bucket bucket = buckets.computeIfAbsent(key, this::createBucket);
        boolean allowed = bucket.tryConsume(tokens);
        
        if (!allowed) {
            logger.warn("Rate limit exceeded for key: {} (requested {} tokens)", key, tokens);
        }
        
        return allowed;
    }

    public long getAvailableTokens(String key) {
        if (!rateLimitingEnabled) {
            return Long.MAX_VALUE;
        }
        
        Bucket bucket = buckets.computeIfAbsent(key, this::createBucket);
        return bucket.getAvailableTokens();
    }

    public Duration getTimeToRefill(String key) {
        if (!rateLimitingEnabled) {
            return Duration.ZERO;
        }
        
        Bucket bucket = buckets.computeIfAbsent(key, this::createBucket);
        return Duration.ofNanos(bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill());
    }

    private Bucket createBucket(String key) {
        logger.debug("Creating rate limit bucket for key: {}", key);
        
        // Create bandwidth limits
        Bandwidth minuteLimit = Bandwidth.classic(requestsPerMinute, Refill.intervally(requestsPerMinute, Duration.ofMinutes(1)));
        Bandwidth hourLimit = Bandwidth.classic(requestsPerHour, Refill.intervally(requestsPerHour, Duration.ofHours(1)));
        
        return Bucket4j.builder()
                .addLimit(minuteLimit)
                .addLimit(hourLimit)
                .build();
    }

    public void clearBucket(String key) {
        logger.debug("Clearing rate limit bucket for key: {}", key);
        buckets.remove(key);
    }

    public void clearAllBuckets() {
        logger.info("Clearing all rate limit buckets");
        buckets.clear();
    }

    public int getBucketCount() {
        return buckets.size();
    }

    // Predefined rate limiting methods for different operations
    public boolean isUrlCreationAllowed(String userKey) {
        return isAllowed("url_creation:" + userKey);
    }

    public boolean isUrlAccessAllowed(String ipKey) {
        return isAllowed("url_access:" + ipKey, 1);
    }

    public boolean isAnalyticsAccessAllowed(String userKey) {
        return isAllowed("analytics:" + userKey);
    }

    public boolean isBulkOperationAllowed(String userKey, int operationCount) {
        return isAllowed("bulk:" + userKey, operationCount);
    }

    // Custom rate limits for premium users
    public boolean isPremiumUserAllowed(String userKey) {
        String premiumKey = "premium:" + userKey;
        Bucket bucket = buckets.computeIfAbsent(premiumKey, k -> createPremiumBucket());
        return bucket.tryConsume(1);
    }

    private Bucket createPremiumBucket() {
        // Premium users get higher limits
        Bandwidth minuteLimit = Bandwidth.classic(requestsPerMinute * 5, Refill.intervally(requestsPerMinute * 5, Duration.ofMinutes(1)));
        Bandwidth hourLimit = Bandwidth.classic(requestsPerHour * 5, Refill.intervally(requestsPerHour * 5, Duration.ofHours(1)));
        
        return Bucket4j.builder()
                .addLimit(minuteLimit)
                .addLimit(hourLimit)
                .build();
    }

    // Admin operations (no rate limiting)
    public boolean isAdminOperationAllowed(String adminKey) {
        // Admins are not rate limited
        return true;
    }
}