package com.example.urlshortener.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "url_analytics", indexes = {
    @Index(name = "idx_url_id", columnList = "urlId"),
    @Index(name = "idx_accessed_at", columnList = "accessedAt"),
    @Index(name = "idx_ip_address", columnList = "ipAddress"),
    @Index(name = "idx_country", columnList = "country")
})
public class UrlAnalytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long urlId;
    
    @Column(nullable = false)
    private LocalDateTime accessedAt;
    
    @Column(length = 45)
    private String ipAddress;
    
    @Column(length = 500)
    private String userAgent;
    
    @Column(length = 100)
    private String browser;
    
    @Column(length = 100)
    private String operatingSystem;
    
    @Column(length = 100)
    private String device;
    
    @Column(length = 100)
    private String country;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 500)
    private String referer;
    
    @Column(length = 100)
    private String language;
    
    @Column
    private Double latitude;
    
    @Column
    private Double longitude;

    public UrlAnalytics() {
        this.accessedAt = LocalDateTime.now();
    }

    public UrlAnalytics(Long urlId, String ipAddress) {
        this();
        this.urlId = urlId;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUrlId() { return urlId; }
    public void setUrlId(Long urlId) { this.urlId = urlId; }

    public LocalDateTime getAccessedAt() { return accessedAt; }
    public void setAccessedAt(LocalDateTime accessedAt) { this.accessedAt = accessedAt; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }

    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }

    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getReferer() { return referer; }
    public void setReferer(String referer) { this.referer = referer; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    @Override
    public String toString() {
        return "UrlAnalytics{" +
                "id=" + id +
                ", urlId=" + urlId +
                ", accessedAt=" + accessedAt +
                ", ipAddress='" + ipAddress + '\'' +
                ", browser='" + browser + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}