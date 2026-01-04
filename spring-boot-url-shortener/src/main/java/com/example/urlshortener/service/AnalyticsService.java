package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlAnalyticsResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.entity.UrlAnalytics;
import com.example.urlshortener.repository.UrlAnalyticsRepository;
import com.example.urlshortener.repository.UrlRepository;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalyticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    
    @Autowired
    private UrlAnalyticsRepository analyticsRepository;
    
    @Autowired
    private UrlRepository urlRepository;
    
    @Autowired
    private GeoLocationService geoLocationService;
    
    @Value("${url-shortener.analytics.enabled}")
    private boolean analyticsEnabled;

    @Async
    public void recordClick(String shortCode, String clientIp, String userAgent, String referer, String language) {
        if (!analyticsEnabled) {
            return;
        }
        
        logger.debug("Recording analytics for short code: {}", shortCode);
        
        try {
            Optional<Url> urlOpt = urlRepository.findByShortCode(shortCode);
            if (urlOpt.isEmpty() || !urlOpt.get().getAnalyticsEnabled()) {
                return;
            }
            
            Url url = urlOpt.get();
            
            // Create analytics record
            UrlAnalytics analytics = new UrlAnalytics(url.getId(), clientIp);
            analytics.setUserAgent(userAgent);
            analytics.setReferer(referer);
            analytics.setLanguage(language);
            
            // Parse user agent
            if (userAgent != null) {
                UserAgent ua = UserAgent.parseUserAgentString(userAgent);
                analytics.setBrowser(ua.getBrowser().getName());
                analytics.setOperatingSystem(ua.getOperatingSystem().getName());
                analytics.setDevice(ua.getOperatingSystem().getDeviceType().getName());
            }
            
            // Get geographic information
            if (clientIp != null && !isLocalIp(clientIp)) {
                try {
                    GeoLocationService.LocationInfo locationInfo = geoLocationService.getLocationInfo(clientIp);
                    if (locationInfo != null) {
                        analytics.setCountry(locationInfo.getCountry());
                        analytics.setCity(locationInfo.getCity());
                        analytics.setLatitude(locationInfo.getLatitude());
                        analytics.setLongitude(locationInfo.getLongitude());
                    }
                } catch (Exception e) {
                    logger.warn("Failed to get location info for IP: {}", clientIp, e);
                }
            }
            
            // Save analytics
            analyticsRepository.save(analytics);
            
            logger.debug("Analytics recorded successfully for URL: {}", url.getId());
            
        } catch (Exception e) {
            logger.error("Error recording analytics for short code: {}", shortCode, e);
        }
    }

    public UrlAnalyticsResponse getUrlAnalytics(String shortCode, String createdBy) {
        logger.info("Getting analytics for short code: {} by user: {}", shortCode, createdBy);
        
        Optional<Url> urlOpt = urlRepository.findByShortCode(shortCode);
        if (urlOpt.isEmpty()) {
            throw new IllegalArgumentException("URL not found");
        }
        
        Url url = urlOpt.get();
        if (!url.getCreatedBy().equals(createdBy)) {
            throw new IllegalArgumentException("Unauthorized to view analytics");
        }
        
        return buildAnalyticsResponse(url);
    }

    public UrlAnalyticsResponse getUrlAnalytics(Long urlId, String createdBy) {
        logger.info("Getting analytics for URL ID: {} by user: {}", urlId, createdBy);
        
        Optional<Url> urlOpt = urlRepository.findById(urlId);
        if (urlOpt.isEmpty()) {
            throw new IllegalArgumentException("URL not found");
        }
        
        Url url = urlOpt.get();
        if (!url.getCreatedBy().equals(createdBy)) {
            throw new IllegalArgumentException("Unauthorized to view analytics");
        }
        
        return buildAnalyticsResponse(url);
    }

    private UrlAnalyticsResponse buildAnalyticsResponse(Url url) {
        UrlAnalyticsResponse response = new UrlAnalyticsResponse(url.getId(), url.getShortCode(), url.getOriginalUrl());
        
        // Basic stats
        response.setTotalClicks(url.getClickCount());
        response.setUniqueVisitors(analyticsRepository.getUniqueVisitors(url.getId()));
        response.setCreatedAt(url.getCreatedAt());
        response.setLastAccessedAt(url.getLastAccessedAt());
        
        // Time-based analytics
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        
        response.setDailyStats(getDailyClickStats(url.getId(), thirtyDaysAgo));
        response.setHourlyStats(getHourlyClickStats(url.getId(), twentyFourHoursAgo));
        
        // Geographic analytics
        response.setCountryStats(getCountryStats(url.getId()));
        
        // Technology analytics
        response.setBrowserStats(getBrowserStats(url.getId()));
        response.setOperatingSystemStats(getOperatingSystemStats(url.getId()));
        response.setDeviceStats(getDeviceStats(url.getId()));
        
        // Referrer analytics
        response.setReferrerStats(getReferrerStats(url.getId()));
        
        // Recent clicks
        response.setRecentClicks(getRecentClicks(url.getId()));
        
        return response;
    }

    private List<UrlAnalyticsResponse.DailyClickStats> getDailyClickStats(Long urlId, LocalDateTime startDate) {
        List<Object[]> results = analyticsRepository.getDailyClickStats(urlId, startDate);
        return results.stream()
                .map(result -> new UrlAnalyticsResponse.DailyClickStats(
                    result[0].toString(),
                    ((Number) result[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    private List<UrlAnalyticsResponse.HourlyClickStats> getHourlyClickStats(Long urlId, LocalDateTime startDate) {
        List<Object[]> results = analyticsRepository.getHourlyClickStats(urlId, startDate);
        return results.stream()
                .map(result -> new UrlAnalyticsResponse.HourlyClickStats(
                    ((Number) result[0]).intValue(),
                    ((Number) result[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    private List<UrlAnalyticsResponse.CountryStats> getCountryStats(Long urlId) {
        List<Object[]> results = analyticsRepository.getClicksByCountry(urlId);
        return results.stream()
                .map(result -> new UrlAnalyticsResponse.CountryStats(
                    (String) result[0],
                    ((Number) result[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    private List<UrlAnalyticsResponse.BrowserStats> getBrowserStats(Long urlId) {
        List<Object[]> results = analyticsRepository.getClicksByBrowser(urlId);
        return results.stream()
                .map(result -> new UrlAnalyticsResponse.BrowserStats(
                    (String) result[0],
                    ((Number) result[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    private List<UrlAnalyticsResponse.OperatingSystemStats> getOperatingSystemStats(Long urlId) {
        List<Object[]> results = analyticsRepository.getClicksByOperatingSystem(urlId);
        return results.stream()
                .map(result -> new UrlAnalyticsResponse.OperatingSystemStats(
                    (String) result[0],
                    ((Number) result[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    private List<UrlAnalyticsResponse.DeviceStats> getDeviceStats(Long urlId) {
        List<Object[]> results = analyticsRepository.getClicksByDevice(urlId);
        return results.stream()
                .map(result -> new UrlAnalyticsResponse.DeviceStats(
                    (String) result[0],
                    ((Number) result[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    private List<UrlAnalyticsResponse.ReferrerStats> getReferrerStats(Long urlId) {
        List<Object[]> results = analyticsRepository.getClicksByReferer(urlId);
        return results.stream()
                .map(result -> new UrlAnalyticsResponse.ReferrerStats(
                    (String) result[0],
                    ((Number) result[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    private List<UrlAnalyticsResponse.RecentClick> getRecentClicks(Long urlId) {
        List<UrlAnalytics> recentAnalytics = analyticsRepository.getRecentAnalytics(urlId, PageRequest.of(0, 10)).getContent();
        return recentAnalytics.stream()
                .map(analytics -> new UrlAnalyticsResponse.RecentClick(
                    analytics.getAccessedAt(),
                    analytics.getCountry(),
                    analytics.getBrowser(),
                    analytics.getOperatingSystem(),
                    analytics.getReferer()
                ))
                .collect(Collectors.toList());
    }

    public void deleteAnalytics(Long urlId) {
        logger.info("Deleting analytics for URL ID: {}", urlId);
        analyticsRepository.deleteByUrlId(urlId);
    }

    @Transactional
    public void cleanupOldAnalytics(int daysToKeep) {
        logger.info("Cleaning up analytics older than {} days", daysToKeep);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        analyticsRepository.deleteOldAnalytics(cutoffDate);
        
        logger.info("Cleaned up analytics older than {}", cutoffDate);
    }

    private boolean isLocalIp(String ip) {
        return ip.equals("127.0.0.1") || 
               ip.equals("localhost") || 
               ip.equals("0:0:0:0:0:0:0:1") ||
               ip.startsWith("192.168.") ||
               ip.startsWith("10.") ||
               ip.startsWith("172.");
    }
}