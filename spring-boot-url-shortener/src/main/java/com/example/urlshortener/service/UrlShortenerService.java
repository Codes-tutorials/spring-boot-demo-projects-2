package com.example.urlshortener.service;

import com.example.urlshortener.dto.ShortenUrlRequest;
import com.example.urlshortener.dto.ShortenUrlResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class UrlShortenerService {
    
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);
    
    @Autowired
    private UrlRepository urlRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${url-shortener.base-url}")
    private String baseUrl;
    
    @Value("${url-shortener.short-code.length}")
    private int shortCodeLength;
    
    @Value("${url-shortener.short-code.characters}")
    private String allowedCharacters;
    
    @Value("${url-shortener.expiration.default-days}")
    private int defaultExpirationDays;
    
    private final Random random = new Random();

    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request, String createdBy, String clientIp, String userAgent) {
        logger.info("Shortening URL: {} for user: {}", request.getUrl(), createdBy);
        
        // Validate URL
        if (!isValidUrl(request.getUrl())) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        
        // Generate or validate short code
        String shortCode;
        if (request.hasCustomAlias()) {
            shortCode = request.getCustomAlias();
            if (urlRepository.existsByShortCode(shortCode)) {
                throw new IllegalArgumentException("Custom alias already exists");
            }
        } else {
            shortCode = generateUniqueShortCode();
        }
        
        // Create URL entity
        Url url = new Url(shortCode, request.getUrl(), createdBy);
        url.setTitle(request.getTitle());
        url.setDescription(request.getDescription());
        url.setIsCustom(request.hasCustomAlias());
        url.setCreatedFromIp(clientIp);
        url.setUserAgent(userAgent);
        url.setAnalyticsEnabled(request.getAnalyticsEnabled());
        
        // Set expiration
        if (request.getExpiresAt() != null) {
            url.setExpiresAt(request.getExpiresAt());
        } else {
            url.setExpiresAt(LocalDateTime.now().plusDays(defaultExpirationDays));
        }
        
        // Set password if provided
        if (request.hasPassword()) {
            url.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Save URL
        Url savedUrl = urlRepository.save(url);
        
        logger.info("URL shortened successfully: {} -> {}", request.getUrl(), shortCode);
        return new ShortenUrlResponse(savedUrl, baseUrl);
    }

    @Cacheable(value = "urls", key = "#shortCode")
    public Optional<Url> findByShortCode(String shortCode) {
        logger.debug("Finding URL by short code: {}", shortCode);
        return urlRepository.findByShortCodeAndIsActiveTrue(shortCode);
    }

    public String expandUrl(String shortCode, String password) {
        logger.info("Expanding URL for short code: {}", shortCode);
        
        Optional<Url> urlOpt = findByShortCode(shortCode);
        if (urlOpt.isEmpty()) {
            throw new IllegalArgumentException("Short URL not found");
        }
        
        Url url = urlOpt.get();
        
        // Check if URL is expired
        if (url.isExpired()) {
            throw new IllegalArgumentException("Short URL has expired");
        }
        
        // Check password if required
        if (url.isPasswordProtected()) {
            if (password == null || !passwordEncoder.matches(password, url.getPassword())) {
                throw new IllegalArgumentException("Invalid password");
            }
        }
        
        return url.getOriginalUrl();
    }

    @Transactional
    public void recordClick(String shortCode, String clientIp, String userAgent, String referer) {
        logger.debug("Recording click for short code: {}", shortCode);
        
        Optional<Url> urlOpt = urlRepository.findByShortCode(shortCode);
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            urlRepository.incrementClickCount(url.getId(), LocalDateTime.now());
            
            // Clear cache to ensure fresh data
            evictUrlFromCache(shortCode);
        }
    }

    public List<ShortenUrlResponse> getUserUrls(String createdBy) {
        logger.info("Getting URLs for user: {}", createdBy);
        
        List<Url> urls = urlRepository.findByCreatedByAndIsActiveTrue(createdBy);
        return urls.stream()
                .map(url -> new ShortenUrlResponse(url, baseUrl))
                .collect(Collectors.toList());
    }

    public Page<ShortenUrlResponse> getUserUrls(String createdBy, Pageable pageable) {
        logger.info("Getting paginated URLs for user: {}", createdBy);
        
        Page<Url> urls = urlRepository.findByCreatedByAndIsActiveTrue(createdBy, pageable);
        return urls.map(url -> new ShortenUrlResponse(url, baseUrl));
    }

    public Optional<ShortenUrlResponse> getUrlDetails(Long id, String createdBy) {
        logger.info("Getting URL details for ID: {} by user: {}", id, createdBy);
        
        Optional<Url> urlOpt = urlRepository.findById(id);
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            if (url.getCreatedBy().equals(createdBy)) {
                return Optional.of(new ShortenUrlResponse(url, baseUrl));
            }
        }
        return Optional.empty();
    }

    @CacheEvict(value = "urls", key = "#shortCode")
    public boolean deleteUrl(String shortCode, String createdBy) {
        logger.info("Deleting URL with short code: {} by user: {}", shortCode, createdBy);
        
        Optional<Url> urlOpt = urlRepository.findByShortCode(shortCode);
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            if (url.getCreatedBy().equals(createdBy)) {
                url.setIsActive(false);
                urlRepository.save(url);
                return true;
            }
        }
        return false;
    }

    public ShortenUrlResponse updateUrl(Long id, ShortenUrlRequest request, String createdBy) {
        logger.info("Updating URL with ID: {} by user: {}", id, createdBy);
        
        Optional<Url> urlOpt = urlRepository.findById(id);
        if (urlOpt.isEmpty()) {
            throw new IllegalArgumentException("URL not found");
        }
        
        Url url = urlOpt.get();
        if (!url.getCreatedBy().equals(createdBy)) {
            throw new IllegalArgumentException("Unauthorized to update this URL");
        }
        
        // Update fields
        url.setTitle(request.getTitle());
        url.setDescription(request.getDescription());
        url.setAnalyticsEnabled(request.getAnalyticsEnabled());
        
        if (request.getExpiresAt() != null) {
            url.setExpiresAt(request.getExpiresAt());
        }
        
        if (request.hasPassword()) {
            url.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            url.setPassword(null);
        }
        
        Url updatedUrl = urlRepository.save(url);
        
        // Clear cache
        evictUrlFromCache(url.getShortCode());
        
        return new ShortenUrlResponse(updatedUrl, baseUrl);
    }

    public Page<ShortenUrlResponse> getPopularUrls(Pageable pageable) {
        logger.info("Getting popular URLs");
        
        Page<Url> urls = urlRepository.findMostPopularUrls(pageable);
        return urls.map(url -> new ShortenUrlResponse(url, baseUrl));
    }

    public Page<ShortenUrlResponse> getRecentUrls(Pageable pageable) {
        logger.info("Getting recent URLs");
        
        Page<Url> urls = urlRepository.findRecentUrls(pageable);
        return urls.map(url -> new ShortenUrlResponse(url, baseUrl));
    }

    @Transactional
    public int cleanupExpiredUrls() {
        logger.info("Cleaning up expired URLs");
        
        int deactivatedCount = urlRepository.deactivateExpiredUrls(LocalDateTime.now());
        logger.info("Deactivated {} expired URLs", deactivatedCount);
        
        return deactivatedCount;
    }

    private String generateUniqueShortCode() {
        String shortCode;
        int attempts = 0;
        int maxAttempts = 10;
        
        do {
            shortCode = generateRandomShortCode();
            attempts++;
            
            if (attempts > maxAttempts) {
                // If we can't find a unique code, increase length
                shortCode = generateRandomShortCode(shortCodeLength + 1);
                break;
            }
        } while (urlRepository.existsByShortCode(shortCode));
        
        return shortCode;
    }

    private String generateRandomShortCode() {
        return generateRandomShortCode(shortCodeLength);
    }

    private String generateRandomShortCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allowedCharacters.length());
            sb.append(allowedCharacters.charAt(index));
        }
        return sb.toString();
    }

    private boolean isValidUrl(String url) {
        return url != null && 
               url.matches("^https?://.*") && 
               url.length() <= 2048;
    }

    @CacheEvict(value = "urls", key = "#shortCode")
    private void evictUrlFromCache(String shortCode) {
        logger.debug("Evicting URL from cache: {}", shortCode);
    }

    // Statistics methods
    public long getTotalUrls() {
        return urlRepository.countActiveUrls();
    }

    public long getTotalClicks() {
        Long totalClicks = urlRepository.getTotalClicks();
        return totalClicks != null ? totalClicks : 0L;
    }

    public long getUserTotalClicks(String createdBy) {
        Long totalClicks = urlRepository.getTotalClicksByUser(createdBy);
        return totalClicks != null ? totalClicks : 0L;
    }

    public long getUserUrlCount(String createdBy, LocalDateTime since) {
        return urlRepository.countByCreatedByAndCreatedAtAfter(createdBy, since);
    }
}