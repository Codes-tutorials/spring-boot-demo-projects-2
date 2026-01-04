package com.example.urlshortener.repository;

import com.example.urlshortener.entity.UrlAnalytics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlAnalyticsRepository extends JpaRepository<UrlAnalytics, Long> {
    
    List<UrlAnalytics> findByUrlId(Long urlId);
    
    Page<UrlAnalytics> findByUrlId(Long urlId, Pageable pageable);
    
    List<UrlAnalytics> findByUrlIdAndAccessedAtBetween(Long urlId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(ua) FROM UrlAnalytics ua WHERE ua.urlId = :urlId")
    Long countByUrlId(@Param("urlId") Long urlId);
    
    @Query("SELECT COUNT(ua) FROM UrlAnalytics ua WHERE ua.urlId = :urlId AND ua.accessedAt >= :startDate")
    Long countByUrlIdAndAccessedAtAfter(@Param("urlId") Long urlId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT ua.country, COUNT(ua) as count FROM UrlAnalytics ua WHERE ua.urlId = :urlId AND ua.country IS NOT NULL GROUP BY ua.country ORDER BY count DESC")
    List<Object[]> getClicksByCountry(@Param("urlId") Long urlId);
    
    @Query("SELECT ua.browser, COUNT(ua) as count FROM UrlAnalytics ua WHERE ua.urlId = :urlId AND ua.browser IS NOT NULL GROUP BY ua.browser ORDER BY count DESC")
    List<Object[]> getClicksByBrowser(@Param("urlId") Long urlId);
    
    @Query("SELECT ua.operatingSystem, COUNT(ua) as count FROM UrlAnalytics ua WHERE ua.urlId = :urlId AND ua.operatingSystem IS NOT NULL GROUP BY ua.operatingSystem ORDER BY count DESC")
    List<Object[]> getClicksByOperatingSystem(@Param("urlId") Long urlId);
    
    @Query("SELECT ua.device, COUNT(ua) as count FROM UrlAnalytics ua WHERE ua.urlId = :urlId AND ua.device IS NOT NULL GROUP BY ua.device ORDER BY count DESC")
    List<Object[]> getClicksByDevice(@Param("urlId") Long urlId);
    
    @Query("SELECT DATE(ua.accessedAt) as date, COUNT(ua) as count FROM UrlAnalytics ua WHERE ua.urlId = :urlId AND ua.accessedAt >= :startDate GROUP BY DATE(ua.accessedAt) ORDER BY date")
    List<Object[]> getDailyClickStats(@Param("urlId") Long urlId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT HOUR(ua.accessedAt) as hour, COUNT(ua) as count FROM UrlAnalytics ua WHERE ua.urlId = :urlId AND ua.accessedAt >= :startDate GROUP BY HOUR(ua.accessedAt) ORDER BY hour")
    List<Object[]> getHourlyClickStats(@Param("urlId") Long urlId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT ua.referer, COUNT(ua) as count FROM UrlAnalytics ua WHERE ua.urlId = :urlId AND ua.referer IS NOT NULL AND ua.referer != '' GROUP BY ua.referer ORDER BY count DESC")
    List<Object[]> getClicksByReferer(@Param("urlId") Long urlId);
    
    @Query("SELECT COUNT(DISTINCT ua.ipAddress) FROM UrlAnalytics ua WHERE ua.urlId = :urlId")
    Long getUniqueVisitors(@Param("urlId") Long urlId);
    
    @Query("SELECT COUNT(DISTINCT ua.ipAddress) FROM UrlAnalytics ua WHERE ua.urlId = :urlId AND ua.accessedAt >= :startDate")
    Long getUniqueVisitorsAfter(@Param("urlId") Long urlId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT ua FROM UrlAnalytics ua WHERE ua.urlId = :urlId ORDER BY ua.accessedAt DESC")
    Page<UrlAnalytics> getRecentAnalytics(@Param("urlId") Long urlId, Pageable pageable);
    
    void deleteByUrlId(Long urlId);
    
    @Query("DELETE FROM UrlAnalytics ua WHERE ua.accessedAt < :cutoffDate")
    void deleteOldAnalytics(@Param("cutoffDate") LocalDateTime cutoffDate);
}