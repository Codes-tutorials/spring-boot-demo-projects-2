package com.example.urlshortener.dto;

import com.example.urlshortener.entity.Url;
import java.time.LocalDateTime;

public class ShortenUrlResponse {
    
    private Long id;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long clickCount;
    private Boolean isCustom;
    private Boolean isPasswordProtected;
    private Boolean analyticsEnabled;

    public ShortenUrlResponse() {}

    public ShortenUrlResponse(Url url, String baseUrl) {
        this.id = url.getId();
        this.shortCode = url.getShortCode();
        this.shortUrl = baseUrl + "/" + url.getShortCode();
        this.originalUrl = url.getOriginalUrl();
        this.title = url.getTitle();
        this.description = url.getDescription();
        this.createdAt = url.getCreatedAt();
        this.expiresAt = url.getExpiresAt();
        this.clickCount = url.getClickCount();
        this.isCustom = url.getIsCustom();
        this.isPasswordProtected = url.isPasswordProtected();
        this.analyticsEnabled = url.getAnalyticsEnabled();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getShortUrl() { return shortUrl; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Long getClickCount() { return clickCount; }
    public void setClickCount(Long clickCount) { this.clickCount = clickCount; }

    public Boolean getIsCustom() { return isCustom; }
    public void setIsCustom(Boolean isCustom) { this.isCustom = isCustom; }

    public Boolean getIsPasswordProtected() { return isPasswordProtected; }
    public void setIsPasswordProtected(Boolean isPasswordProtected) { this.isPasswordProtected = isPasswordProtected; }

    public Boolean getAnalyticsEnabled() { return analyticsEnabled; }
    public void setAnalyticsEnabled(Boolean analyticsEnabled) { this.analyticsEnabled = analyticsEnabled; }

    @Override
    public String toString() {
        return "ShortenUrlResponse{" +
                "id=" + id +
                ", shortCode='" + shortCode + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", clickCount=" + clickCount +
                '}';
    }
}