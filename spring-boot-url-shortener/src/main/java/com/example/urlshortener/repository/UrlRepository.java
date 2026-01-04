package com.example.urlshortener.repository;

import com.example.urlshortener.entity.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    
    Optional<Url> findByShortCode(String shortCode);
    
    Optional<Url> findByShortCodeAndIsActiveTrue(String shortCode);
    
    List<Url> findByCreatedBy(String createdBy);
    
    Page<Url> findByCreatedBy(String createdBy, Pageable pageable);
    
    List<Url> findByCreatedByAndIsActiveTrue(String createdBy);
    
    Page<Url> findByCreatedByAndIsActiveTrue(String createdBy, Pageable pageable);
    
    List<Url> findByOriginalUrl(String originalUrl);
    
    @Query("SELECT u FROM Url u WHERE u.expiresAt < :now AND u.isActive = true")
    List<Url> findExpiredUrls(@Param("now") LocalDateTime now);
    
    @Query("SELECT u FROM Url u WHERE u.createdBy = :createdBy AND u.createdAt >= :startDate")
    List<Url> findByCreatedByAndCreatedAtAfter(@Param("createdBy") String createdBy, 
                                               @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(u) FROM Url u WHERE u.createdBy = :createdBy AND u.createdAt >= :startDate")
    Long countByCreatedByAndCreatedAtAfter(@Param("createdBy") String createdBy, 
                                          @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT u FROM Url u ORDER BY u.clickCount DESC")
    Page<Url> findMostPopularUrls(Pageable pageable);
    
    @Query("SELECT u FROM Url u WHERE u.createdBy = :createdBy ORDER BY u.clickCount DESC")
    Page<Url> findMostPopularUrlsByUser(@Param("createdBy") String createdBy, Pageable pageable);
    
    @Query("SELECT u FROM Url u ORDER BY u.createdAt DESC")
    Page<Url> findRecentUrls(Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM Url u WHERE u.isActive = true")
    Long countActiveUrls();
    
    @Query("SELECT SUM(u.clickCount) FROM Url u WHERE u.isActive = true")
    Long getTotalClicks();
    
    @Query("SELECT SUM(u.clickCount) FROM Url u WHERE u.createdBy = :createdBy AND u.isActive = true")
    Long getTotalClicksByUser(@Param("createdBy") String createdBy);
    
    @Modifying
    @Query("UPDATE Url u SET u.clickCount = u.clickCount + 1, u.lastAccessedAt = :accessTime WHERE u.id = :id")
    void incrementClickCount(@Param("id") Long id, @Param("accessTime") LocalDateTime accessTime);
    
    @Modifying
    @Query("UPDATE Url u SET u.isActive = false WHERE u.expiresAt < :now AND u.isActive = true")
    int deactivateExpiredUrls(@Param("now") LocalDateTime now);
    
    boolean existsByShortCode(String shortCode);
    
    boolean existsByShortCodeAndIsActiveTrue(String shortCode);
    
    @Query("SELECT u.shortCode FROM Url u WHERE u.shortCode LIKE :prefix%")
    List<String> findShortCodesByPrefix(@Param("prefix") String prefix);
}