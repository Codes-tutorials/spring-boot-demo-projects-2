package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlAnalyticsResponse;
import com.example.urlshortener.service.AnalyticsService;
import com.example.urlshortener.service.RateLimitingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private RateLimitingService rateLimitingService;

    @GetMapping("/url/{shortCode}")
    public ResponseEntity<?> getUrlAnalytics(@PathVariable String shortCode, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String userKey = authentication.getName();
        
        // Check rate limiting
        if (!rateLimitingService.isAnalyticsAccessAllowed(userKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit exceeded. Please try again later."));
        }
        
        try {
            UrlAnalyticsResponse analytics = analyticsService.getUrlAnalytics(shortCode, userKey);
            return ResponseEntity.ok(analytics);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid analytics request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting analytics for short code: {}", shortCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/url/id/{urlId}")
    public ResponseEntity<?> getUrlAnalyticsById(@PathVariable Long urlId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String userKey = authentication.getName();
        
        // Check rate limiting
        if (!rateLimitingService.isAnalyticsAccessAllowed(userKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit exceeded. Please try again later."));
        }
        
        try {
            UrlAnalyticsResponse analytics = analyticsService.getUrlAnalytics(urlId, userKey);
            return ResponseEntity.ok(analytics);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid analytics request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting analytics for URL ID: {}", urlId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/url/id/{urlId}")
    public ResponseEntity<?> deleteUrlAnalytics(@PathVariable Long urlId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            // Note: In a real application, you'd want to verify the user owns this URL
            analyticsService.deleteAnalytics(urlId);
            return ResponseEntity.ok(Map.of("message", "Analytics deleted successfully"));
            
        } catch (Exception e) {
            logger.error("Error deleting analytics for URL ID: {}", urlId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}