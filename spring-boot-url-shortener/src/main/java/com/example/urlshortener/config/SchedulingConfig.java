package com.example.urlshortener.config;

import com.example.urlshortener.service.AnalyticsService;
import com.example.urlshortener.service.UrlShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class SchedulingConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);
    
    @Autowired
    private UrlShortenerService urlShortenerService;
    
    @Autowired
    private AnalyticsService analyticsService;

    // Clean up expired URLs every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredUrls() {
        logger.info("Starting scheduled cleanup of expired URLs");
        try {
            int cleanedUp = urlShortenerService.cleanupExpiredUrls();
            logger.info("Cleaned up {} expired URLs", cleanedUp);
        } catch (Exception e) {
            logger.error("Error during expired URL cleanup", e);
        }
    }

    // Clean up old analytics data every day (keep last 90 days)
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void cleanupOldAnalytics() {
        logger.info("Starting scheduled cleanup of old analytics data");
        try {
            analyticsService.cleanupOldAnalytics(90); // Keep last 90 days
            logger.info("Cleaned up old analytics data");
        } catch (Exception e) {
            logger.error("Error during analytics cleanup", e);
        }
    }

    // Log system statistics every 6 hours
    @Scheduled(fixedRate = 21600000) // 6 hours
    public void logSystemStats() {
        try {
            long totalUrls = urlShortenerService.getTotalUrls();
            long totalClicks = urlShortenerService.getTotalClicks();
            
            logger.info("System Stats - Total URLs: {}, Total Clicks: {}", totalUrls, totalClicks);
        } catch (Exception e) {
            logger.error("Error logging system stats", e);
        }
    }
}