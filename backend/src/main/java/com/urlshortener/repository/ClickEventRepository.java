package com.urlshortener.repository;

import com.urlshortener.entity.ClickEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Click Event entity operations
 */
@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    /**
     * Find all click events for a URL
     */
    List<ClickEvent> findByUrlId(Long urlId);

    /**
     * Find click events for a URL with pagination
     */
    Page<ClickEvent> findByUrlIdOrderByClickedAtDesc(Long urlId, Pageable pageable);

    /**
     * Count unique IP addresses for a URL
     */
    @Query("SELECT COUNT(DISTINCT c.ipAddress) FROM ClickEvent c WHERE c.urlId = :urlId")
    Long countUniqueVisitorsByUrlId(@Param("urlId") Long urlId);

    /**
     * Get click count by browser
     */
    @Query("SELECT c.browser, COUNT(c) FROM ClickEvent c WHERE c.urlId = :urlId GROUP BY c.browser ORDER BY COUNT(c) DESC")
    List<Object[]> getBrowserStats(@Param("urlId") Long urlId);

    /**
     * Get click count by OS
     */
    @Query("SELECT c.os, COUNT(c) FROM ClickEvent c WHERE c.urlId = :urlId GROUP BY c.os ORDER BY COUNT(c) DESC")
    List<Object[]> getOsStats(@Param("urlId") Long urlId);

    /**
     * Get click count by device
     */
    @Query("SELECT c.device, COUNT(c) FROM ClickEvent c WHERE c.urlId = :urlId GROUP BY c.device ORDER BY COUNT(c) DESC")
    List<Object[]> getDeviceStats(@Param("urlId") Long urlId);

    /**
     * Get click count by referer
     */
    @Query("SELECT c.referer, COUNT(c) FROM ClickEvent c WHERE c.urlId = :urlId AND c.referer IS NOT NULL GROUP BY c.referer ORDER BY COUNT(c) DESC")
    List<Object[]> getReferrerStats(@Param("urlId") Long urlId);

    /**
     * Get click count by country
     */
    @Query("SELECT c.country, COUNT(c) FROM ClickEvent c WHERE c.urlId = :urlId AND c.country IS NOT NULL GROUP BY c.country ORDER BY COUNT(c) DESC")
    List<Object[]> getCountryStats(@Param("urlId") Long urlId);

    /**
     * Get daily click counts for a URL
     */
    @Query("SELECT DATE(c.clickedAt), COUNT(c) FROM ClickEvent c WHERE c.urlId = :urlId GROUP BY DATE(c.clickedAt) ORDER BY DATE(c.clickedAt)")
    List<Object[]> getDailyClicks(@Param("urlId") Long urlId);

    /**
     * Get clicks in date range
     */
    @Query("SELECT c FROM ClickEvent c WHERE c.urlId = :urlId AND c.clickedAt BETWEEN :start AND :end ORDER BY c.clickedAt DESC")
    List<ClickEvent> findByUrlIdAndDateRange(@Param("urlId") Long urlId, 
                                               @Param("start") LocalDateTime start, 
                                               @Param("end") LocalDateTime end);

    /**
     * Delete old click events
     */
    @Query("DELETE FROM ClickEvent c WHERE c.clickedAt < :date")
    void deleteByClickedAtBefore(@Param("date") LocalDateTime date);
}
