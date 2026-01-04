package com.example.urlshortener.controller;

import com.example.urlshortener.dto.ShortenUrlRequest;
import com.example.urlshortener.dto.ShortenUrlResponse;
import com.example.urlshortener.service.AnalyticsService;
import com.example.urlshortener.service.RateLimitingService;
import com.example.urlshortener.service.UrlShortenerService;
import com.example.urlshortener.util.ClientInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/urls")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class UrlShortenerController {
    
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);
    
    @Autowired
    private UrlShortenerService urlShortenerService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private RateLimitingService rateLimitingService;

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@Valid @RequestBody ShortenUrlRequest request,
                                       HttpServletRequest httpRequest,
                                       Authentication authentication) {
        
        String clientIp = ClientInfoUtil.getClientIpAddress(httpRequest);
        String userKey = authentication != null ? authentication.getName() : clientIp;
        
        // Check rate limiting
        if (!rateLimitingService.isUrlCreationAllowed(userKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit exceeded. Please try again later."));
        }
        
        try {
            String createdBy = authentication != null ? authentication.getName() : clientIp;
            String userAgent = httpRequest.getHeader("User-Agent");
            
            ShortenUrlResponse response = urlShortenerService.shortenUrl(request, createdBy, clientIp, userAgent);
            
            logger.info("URL shortened successfully: {} -> {}", request.getUrl(), response.getShortCode());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for URL shortening: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error shortening URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/my-urls")
    public ResponseEntity<Page<ShortenUrlResponse>> getMyUrls(Pageable pageable, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String createdBy = authentication.getName();
        Page<ShortenUrlResponse> urls = urlShortenerService.getUserUrls(createdBy, pageable);
        
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShortenUrlResponse> getUrlDetails(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String createdBy = authentication.getName();
        Optional<ShortenUrlResponse> url = urlShortenerService.getUrlDetails(id, createdBy);
        
        return url.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUrl(@PathVariable Long id,
                                      @Valid @RequestBody ShortenUrlRequest request,
                                      Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            String createdBy = authentication.getName();
            ShortenUrlResponse response = urlShortenerService.updateUrl(id, request, createdBy);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<?> deleteUrl(@PathVariable String shortCode, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String createdBy = authentication.getName();
        boolean deleted = urlShortenerService.deleteUrl(shortCode, createdBy);
        
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "URL deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<Page<ShortenUrlResponse>> getPopularUrls(Pageable pageable) {
        Page<ShortenUrlResponse> urls = urlShortenerService.getPopularUrls(pageable);
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/recent")
    public ResponseEntity<Page<ShortenUrlResponse>> getRecentUrls(Pageable pageable) {
        Page<ShortenUrlResponse> urls = urlShortenerService.getRecentUrls(pageable);
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(Authentication authentication) {
        Map<String, Object> stats = Map.of(
            "totalUrls", urlShortenerService.getTotalUrls(),
            "totalClicks", urlShortenerService.getTotalClicks()
        );
        
        if (authentication != null) {
            String createdBy = authentication.getName();
            stats = Map.of(
                "totalUrls", urlShortenerService.getTotalUrls(),
                "totalClicks", urlShortenerService.getTotalClicks(),
                "userTotalClicks", urlShortenerService.getUserTotalClicks(createdBy)
            );
        }
        
        return ResponseEntity.ok(stats);
    }

    // Password verification endpoint for password-protected URLs
    @PostMapping("/{shortCode}/verify-password")
    public ResponseEntity<?> verifyPassword(@PathVariable String shortCode,
                                           @RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            String originalUrl = urlShortenerService.expandUrl(shortCode, password);
            
            return ResponseEntity.ok(Map.of("originalUrl", originalUrl));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

// Separate controller for URL redirection (no /api prefix)
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
class RedirectController {
    
    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);
    
    @Autowired
    private UrlShortenerService urlShortenerService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private RateLimitingService rateLimitingService;

    @GetMapping("/{shortCode}")
    public void redirectToOriginalUrl(@PathVariable String shortCode,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {
        
        String clientIp = ClientInfoUtil.getClientIpAddress(request);
        
        // Check rate limiting for URL access
        if (!rateLimitingService.isUrlAccessAllowed(clientIp)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        
        try {
            // Check if URL requires password
            Optional<com.example.urlshortener.entity.Url> urlOpt = urlShortenerService.findByShortCode(shortCode);
            if (urlOpt.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.getWriter().write("Short URL not found");
                return;
            }
            
            com.example.urlshortener.entity.Url url = urlOpt.get();
            
            // If password protected, redirect to password page
            if (url.isPasswordProtected()) {
                response.sendRedirect("/password/" + shortCode);
                return;
            }
            
            // Expand URL
            String originalUrl = urlShortenerService.expandUrl(shortCode, null);
            
            // Record click
            urlShortenerService.recordClick(shortCode, clientIp, 
                request.getHeader("User-Agent"), request.getHeader("Referer"));
            
            // Record analytics
            analyticsService.recordClick(shortCode, clientIp, 
                request.getHeader("User-Agent"), request.getHeader("Referer"),
                request.getHeader("Accept-Language"));
            
            // Redirect
            response.sendRedirect(originalUrl);
            
            logger.info("Redirected {} to {}", shortCode, originalUrl);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid short code access: {}", e.getMessage());
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.getWriter().write(e.getMessage());
        } catch (Exception e) {
            logger.error("Error redirecting short URL: {}", shortCode, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("Internal server error");
        }
    }

    @GetMapping("/password/{shortCode}")
    public void showPasswordPage(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        // In a real application, this would render a password input page
        // For now, we'll return a simple HTML form
        response.setContentType("text/html");
        response.getWriter().write(
            "<html><body>" +
            "<h2>Password Required</h2>" +
            "<p>This short URL is password protected.</p>" +
            "<form action='/api/urls/" + shortCode + "/verify-password' method='post'>" +
            "<input type='password' name='password' placeholder='Enter password' required>" +
            "<button type='submit'>Access URL</button>" +
            "</form>" +
            "</body></html>"
        );
    }
}