package com.example.urlshortener.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class UrlAnalyticsResponse {
    
    private Long urlId;
    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private Long uniqueVisitors;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    
    // Time-based analytics
    private List<DailyClickStats> dailyStats;
    private List<HourlyClickStats> hourlyStats;
    
    // Geographic analytics
    private List<CountryStats> countryStats;
    private List<CityStats> cityStats;
    
    // Technology analytics
    private List<BrowserStats> browserStats;
    private List<OperatingSystemStats> operatingSystemStats;
    private List<DeviceStats> deviceStats;
    
    // Referrer analytics
    private List<ReferrerStats> referrerStats;
    
    // Recent activity
    private List<RecentClick> recentClicks;

    public UrlAnalyticsResponse() {}

    public UrlAnalyticsResponse(Long urlId, String shortCode, String originalUrl) {
        this.urlId = urlId;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
    }

    // Getters and Setters
    public Long getUrlId() { return urlId; }
    public void setUrlId(Long urlId) { this.urlId = urlId; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public Long getTotalClicks() { return totalClicks; }
    public void setTotalClicks(Long totalClicks) { this.totalClicks = totalClicks; }

    public Long getUniqueVisitors() { return uniqueVisitors; }
    public void setUniqueVisitors(Long uniqueVisitors) { this.uniqueVisitors = uniqueVisitors; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }

    public List<DailyClickStats> getDailyStats() { return dailyStats; }
    public void setDailyStats(List<DailyClickStats> dailyStats) { this.dailyStats = dailyStats; }

    public List<HourlyClickStats> getHourlyStats() { return hourlyStats; }
    public void setHourlyStats(List<HourlyClickStats> hourlyStats) { this.hourlyStats = hourlyStats; }

    public List<CountryStats> getCountryStats() { return countryStats; }
    public void setCountryStats(List<CountryStats> countryStats) { this.countryStats = countryStats; }

    public List<CityStats> getCityStats() { return cityStats; }
    public void setCityStats(List<CityStats> cityStats) { this.cityStats = cityStats; }

    public List<BrowserStats> getBrowserStats() { return browserStats; }
    public void setBrowserStats(List<BrowserStats> browserStats) { this.browserStats = browserStats; }

    public List<OperatingSystemStats> getOperatingSystemStats() { return operatingSystemStats; }
    public void setOperatingSystemStats(List<OperatingSystemStats> operatingSystemStats) { this.operatingSystemStats = operatingSystemStats; }

    public List<DeviceStats> getDeviceStats() { return deviceStats; }
    public void setDeviceStats(List<DeviceStats> deviceStats) { this.deviceStats = deviceStats; }

    public List<ReferrerStats> getReferrerStats() { return referrerStats; }
    public void setReferrerStats(List<ReferrerStats> referrerStats) { this.referrerStats = referrerStats; }

    public List<RecentClick> getRecentClicks() { return recentClicks; }
    public void setRecentClicks(List<RecentClick> recentClicks) { this.recentClicks = recentClicks; }

    // Inner classes for statistics
    public static class DailyClickStats {
        private String date;
        private Long clicks;

        public DailyClickStats(String date, Long clicks) {
            this.date = date;
            this.clicks = clicks;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }

    public static class HourlyClickStats {
        private Integer hour;
        private Long clicks;

        public HourlyClickStats(Integer hour, Long clicks) {
            this.hour = hour;
            this.clicks = clicks;
        }

        public Integer getHour() { return hour; }
        public void setHour(Integer hour) { this.hour = hour; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }

    public static class CountryStats {
        private String country;
        private Long clicks;

        public CountryStats(String country, Long clicks) {
            this.country = country;
            this.clicks = clicks;
        }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }

    public static class CityStats {
        private String city;
        private Long clicks;

        public CityStats(String city, Long clicks) {
            this.city = city;
            this.clicks = clicks;
        }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }

    public static class BrowserStats {
        private String browser;
        private Long clicks;

        public BrowserStats(String browser, Long clicks) {
            this.browser = browser;
            this.clicks = clicks;
        }

        public String getBrowser() { return browser; }
        public void setBrowser(String browser) { this.browser = browser; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }

    public static class OperatingSystemStats {
        private String operatingSystem;
        private Long clicks;

        public OperatingSystemStats(String operatingSystem, Long clicks) {
            this.operatingSystem = operatingSystem;
            this.clicks = clicks;
        }

        public String getOperatingSystem() { return operatingSystem; }
        public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }

    public static class DeviceStats {
        private String device;
        private Long clicks;

        public DeviceStats(String device, Long clicks) {
            this.device = device;
            this.clicks = clicks;
        }

        public String getDevice() { return device; }
        public void setDevice(String device) { this.device = device; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }

    public static class ReferrerStats {
        private String referrer;
        private Long clicks;

        public ReferrerStats(String referrer, Long clicks) {
            this.referrer = referrer;
            this.clicks = clicks;
        }

        public String getReferrer() { return referrer; }
        public void setReferrer(String referrer) { this.referrer = referrer; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }

    public static class RecentClick {
        private LocalDateTime timestamp;
        private String country;
        private String browser;
        private String operatingSystem;
        private String referrer;

        public RecentClick(LocalDateTime timestamp, String country, String browser, String operatingSystem, String referrer) {
            this.timestamp = timestamp;
            this.country = country;
            this.browser = browser;
            this.operatingSystem = operatingSystem;
            this.referrer = referrer;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getBrowser() { return browser; }
        public void setBrowser(String browser) { this.browser = browser; }
        public String getOperatingSystem() { return operatingSystem; }
        public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }
        public String getReferrer() { return referrer; }
        public void setReferrer(String referrer) { this.referrer = referrer; }
    }
}