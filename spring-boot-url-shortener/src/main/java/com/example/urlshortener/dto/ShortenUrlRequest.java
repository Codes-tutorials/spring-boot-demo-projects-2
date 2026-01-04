package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ShortenUrlRequest {
    
    @NotBlank(message = "URL is required")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    private String url;
    
    @Size(max = 10, message = "Custom alias cannot exceed 10 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "Custom alias can only contain letters, numbers, hyphens, and underscores")
    private String customAlias;
    
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private LocalDateTime expiresAt;
    
    private String password;
    
    private Boolean analyticsEnabled = true;

    public ShortenUrlRequest() {}

    public ShortenUrlRequest(String url) {
        this.url = url;
    }

    public ShortenUrlRequest(String url, String customAlias) {
        this.url = url;
        this.customAlias = customAlias;
    }

    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getCustomAlias() { return customAlias; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getAnalyticsEnabled() { return analyticsEnabled; }
    public void setAnalyticsEnabled(Boolean analyticsEnabled) { this.analyticsEnabled = analyticsEnabled; }

    public boolean hasCustomAlias() {
        return customAlias != null && !customAlias.trim().isEmpty();
    }

    public boolean hasPassword() {
        return password != null && !password.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "ShortenUrlRequest{" +
                "url='" + url + '\'' +
                ", customAlias='" + customAlias + '\'' +
                ", title='" + title + '\'' +
                ", hasPassword=" + hasPassword() +
                '}';
    }
}