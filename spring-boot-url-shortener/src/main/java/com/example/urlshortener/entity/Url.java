package com.example.urlshortener.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "urls", indexes = {
    @Index(name = "idx_short_code", columnList = "shortCode", unique = true),
    @Index(name = "idx_original_url", columnList = "originalUrl"),
    @Index(name = "idx_created_by", columnList = "createdBy"),
    @Index(name = "idx_expires_at", columnList = "expiresAt")
})
public class Url {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 10)
    private String shortCode;
    
    @Column(nullable = false, length = 2048)
    private String originalUrl;
    
    @Column(length = 255)
    private String title;
    
    @Column(length = 500)
    private String description;
    
    @Column
    private String createdBy; // User ID or IP address
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Boolean isCustom = false;
    
    @Column(nullable = false)
    private Long clickCount = 0L;
    
    @Column
    private LocalDateTime lastAccessedAt;
    
    @Column(length = 45)
    private String createdFromIp;
    
    @Column(length = 500)
    private String userAgent;
    
    @Column(length = 100)
    private String password; // For password-protected URLs
    
    @Column(nullable = false)
    private Boolean analyticsEnabled = true;

    public Url() {
        this.createdAt = LocalDateTime.now();
    }

    public Url(String shortCode, String originalUrl) {
        this();
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
    }

    public Url(String shortCode, String originalUrl, String createdBy) {
        this(shortCode, originalUrl);
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsCustom() { return isCustom; }
    public void setIsCustom(Boolean isCustom) { this.isCustom = isCustom; }

    public Long getClickCount() { return clickCount; }
    public void setClickCount(Long clickCount) { this.clickCount = clickCount; }

    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }

    public String getCreatedFromIp() { return createdFromIp; }
    public void setCreatedFromIp(String createdFromIp) { this.createdFromIp = createdFromIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getAnalyticsEnabled() { return analyticsEnabled; }
    public void setAnalyticsEnabled(Boolean analyticsEnabled) { this.analyticsEnabled = analyticsEnabled; }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isPasswordProtected() {
        return password != null && !password.isEmpty();
    }

    @Override
    public String toString() {
        return "Url{" +
                "id=" + id +
                ", shortCode='" + shortCode + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", clickCount=" + clickCount +
                '}';
    }
}