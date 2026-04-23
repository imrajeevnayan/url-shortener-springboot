package com.urlshortener.repository;

import com.urlshortener.entity.Url;
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

/**
 * Repository for URL entity operations
 */
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    /**
     * Find URL by short code
     */
    Optional<Url> findByShortCode(String shortCode);

    /**
     * Find URL by custom alias
     */
    Optional<Url> findByCustomAlias(String customAlias);

    /**
     * Check if short code exists
     */
    boolean existsByShortCode(String shortCode);

    /**
     * Check if custom alias exists
     */
    boolean existsByCustomAlias(String customAlias);

    /**
     * Find all active URLs with pagination
     */
    Page<Url> findByActiveTrue(Pageable pageable);

    /**
     * Find URLs by original URL
     */
    List<Url> findByOriginalUrlContainingIgnoreCase(String originalUrl);

    /**
     * Find expired URLs
     */
    @Query("SELECT u FROM Url u WHERE u.expiresAt < :now AND u.active = true")
    List<Url> findExpiredUrls(@Param("now") LocalDateTime now);

    /**
     * Deactivate expired URLs
     */
    @Modifying
    @Query("UPDATE Url u SET u.active = false WHERE u.expiresAt < :now AND u.active = true")
    int deactivateExpiredUrls(@Param("now") LocalDateTime now);

    /**
     * Search URLs by keyword in original URL, title, or description
     */
    @Query("SELECT u FROM Url u WHERE " +
           "LOWER(u.originalUrl) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Url> searchUrls(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Get total clicks across all URLs
     */
    @Query("SELECT COALESCE(SUM(u.clickCount), 0) FROM Url u")
    Long getTotalClicks();

    /**
     * Count active URLs
     */
    long countByActiveTrue();

    /**
     * Find recently created URLs
     */
    List<Url> findTop10ByActiveTrueOrderByCreatedAtDesc();
}
