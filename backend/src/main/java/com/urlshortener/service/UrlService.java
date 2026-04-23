package com.urlshortener.service;

import com.urlshortener.dto.*;
import com.urlshortener.entity.ClickEvent;
import com.urlshortener.entity.Url;
import com.urlshortener.repository.ClickEventRepository;
import com.urlshortener.repository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Main service for URL shortening operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final ClickEventRepository clickEventRepository;
    private final Base62Encoder base62Encoder;
    private final QrCodeService qrCodeService;
    private final UserAgentParser userAgentParser;

    @Value("${app.url-shortener.base-url}")
    private String baseUrl;

    @Value("${app.url-shortener.default-expiry-days:30}")
    private int defaultExpiryDays;

    @Value("${app.url-shortener.max-custom-alias-length:50}")
    private int maxCustomAliasLength;

    // ==================== URL CRUD Operations ====================

    /**
     * Create a new shortened URL
     */
    @Transactional
    public UrlResponse createShortUrl(UrlRequest request) {
        // Validate URL
        validateUrl(request.getOriginalUrl());

        // Check for duplicate custom alias
        if (request.getCustomAlias() != null && !request.getCustomAlias().isEmpty()) {
            if (urlRepository.existsByCustomAlias(request.getCustomAlias())) {
                throw new RuntimeException("Custom alias already in use: " + request.getCustomAlias());
            }
            if (urlRepository.existsByShortCode(request.getCustomAlias())) {
                throw new RuntimeException("Alias conflicts with existing short code");
            }
        }

        // Generate unique short code
        String shortCode = generateUniqueShortCode(request.getCustomAlias());

        // Calculate expiration
        LocalDateTime expiresAt = null;
        if (request.getExpiryDays() != null && request.getExpiryDays() > 0) {
            expiresAt = LocalDateTime.now().plusDays(request.getExpiryDays());
        } else if (defaultExpiryDays > 0) {
            expiresAt = LocalDateTime.now().plusDays(defaultExpiryDays);
        }

        // Create and save URL entity
        Url url = Url.builder()
                .shortCode(shortCode)
                .originalUrl(request.getOriginalUrl())
                .customAlias(request.getCustomAlias())
                .title(request.getTitle())
                .description(request.getDescription())
                .expiresAt(expiresAt)
                .password(request.getPassword())
                .active(true)
                .clickCount(0L)
                .build();

        url = urlRepository.save(url);
        log.info("Created short URL: {} -> {}", shortCode, request.getOriginalUrl());

        return mapToResponse(url);
    }

    /**
     * Get URL by short code
     */
    @Transactional(readOnly = true)
    public Optional<UrlResponse> getUrlByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .map(this::mapToResponse);
    }

    /**
     * Get all URLs with pagination
     */
    @Transactional(readOnly = true)
    public Page<UrlResponse> getAllUrls(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return urlRepository.findByActiveTrue(pageable).map(this::mapToResponse);
    }

    /**
     * Get recently created URLs
     */
    @Transactional(readOnly = true)
    public List<UrlResponse> getRecentUrls() {
        return urlRepository.findTop10ByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search URLs by keyword
     */
    @Transactional(readOnly = true)
    public Page<UrlResponse> searchUrls(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return urlRepository.searchUrls(keyword, pageable).map(this::mapToResponse);
    }

    /**
     * Update URL
     */
    @Transactional
    public UrlResponse updateUrl(Long id, UrlRequest request) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found with id: " + id));

        if (request.getOriginalUrl() != null && !request.getOriginalUrl().isEmpty()) {
            validateUrl(request.getOriginalUrl());
            url.setOriginalUrl(request.getOriginalUrl());
        }
        if (request.getTitle() != null) {
            url.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            url.setDescription(request.getDescription());
        }
        if (request.getPassword() != null) {
            url.setPassword(request.getPassword().isEmpty() ? null : request.getPassword());
        }
        if (request.getExpiryDays() != null) {
            if (request.getExpiryDays() > 0) {
                url.setExpiresAt(LocalDateTime.now().plusDays(request.getExpiryDays()));
            } else {
                url.setExpiresAt(null);
            }
        }

        url = urlRepository.save(url);
        return mapToResponse(url);
    }

    /**
     * Delete (deactivate) URL
     */
    @Transactional
    public void deleteUrl(Long id) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found with id: " + id));
        url.setActive(false);
        urlRepository.save(url);
        log.info("Deactivated URL: {}", url.getShortCode());
    }

    /**
     * Permanently delete URL and its analytics
     */
    @Transactional
    public void permanentDeleteUrl(Long id) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found with id: " + id));
        
        // Delete click events first
        List<ClickEvent> events = clickEventRepository.findByUrlId(id);
        clickEventRepository.deleteAll(events);
        
        // Delete URL
        urlRepository.delete(url);
        log.info("Permanently deleted URL: {}", url.getShortCode());
    }

    // ==================== URL Redirection ====================

    /**
     * Get original URL for redirection and track click
     */
    @Transactional
    public String getOriginalUrlAndTrack(String shortCode, HttpServletRequest request) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found: " + shortCode));

        if (!url.getActive()) {
            throw new RuntimeException("URL is inactive: " + shortCode);
        }

        if (url.isExpired()) {
            throw new RuntimeException("URL has expired: " + shortCode);
        }

        // Track click
        trackClickEvent(url.getId(), request);

        // Update URL stats
        url.incrementClickCount();
        url.setLastAccessedAt(LocalDateTime.now());
        urlRepository.save(url);

        return url.getOriginalUrl();
    }

    /**
     * Track click event for analytics
     */
    private void trackClickEvent(Long urlId, HttpServletRequest request) {
        try {
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(request);
            String referer = request.getHeader("Referer");

            UserAgentParser.AgentInfo agentInfo = userAgentParser.parse(userAgent);

            ClickEvent event = ClickEvent.builder()
                    .urlId(urlId)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .referer(referer)
                    .browser(agentInfo.getBrowser())
                    .os(agentInfo.getOs())
                    .device(agentInfo.getDevice())
                    .build();

            clickEventRepository.save(event);
        } catch (Exception e) {
            log.error("Error tracking click event", e);
        }
    }

    // ==================== Analytics ====================

    /**
     * Get analytics for a URL
     */
    @Transactional(readOnly = true)
    public AnalyticsDto getAnalytics(Long urlId) {
        Url url = urlRepository.findById(urlId)
                .orElseThrow(() -> new RuntimeException("URL not found with id: " + urlId));

        Long uniqueVisitors = clickEventRepository.countUniqueVisitorsByUrlId(urlId);
        if (uniqueVisitors == null) uniqueVisitors = 0L;

        return AnalyticsDto.builder()
                .urlId(urlId)
                .shortCode(url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .totalClicks(url.getClickCount())
                .uniqueVisitors(uniqueVisitors)
                .clicksOverTime(getClicksOverTime(urlId))
                .browsers(getBrowserStats(urlId))
                .operatingSystems(getOsStats(urlId))
                .devices(getDeviceStats(urlId))
                .referrers(getReferrerStats(urlId))
                .countries(getCountryStats(urlId))
                .build();
    }

    /**
     * Get dashboard statistics
     */
    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {
        long totalUrls = urlRepository.count();
        long activeUrls = urlRepository.countByActiveTrue();
        Long totalClicks = urlRepository.getTotalClicks();
        if (totalClicks == null) totalClicks = 0L;

        return DashboardStats.builder()
                .totalUrls(totalUrls)
                .activeUrls(activeUrls)
                .totalClicks(totalClicks)
                .recentUrls(getRecentUrls())
                .build();
    }

    // ==================== Helper Methods ====================

    private String generateUniqueShortCode(String customAlias) {
        if (customAlias != null && !customAlias.isEmpty()) {
            return customAlias;
        }

        String shortCode;
        int attempts = 0;
        do {
            shortCode = base62Encoder.generateRandomCode();
            attempts++;
        } while (urlRepository.existsByShortCode(shortCode) && attempts < 10);

        if (attempts >= 10) {
            // Fallback with timestamp
            shortCode = base62Encoder.encode(System.currentTimeMillis());
        }

        return shortCode;
    }

    private void validateUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new RuntimeException("URL cannot be empty");
        }
        if (!url.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")) {
            // Try adding https:// prefix
            if (!url.matches("^www\\..*")) {
                throw new RuntimeException("Invalid URL format");
            }
        }
    }

    private UrlResponse mapToResponse(Url url) {
        String shortUrl = baseUrl + "/" + url.getShortCode();

        return UrlResponse.builder()
                .id(url.getId())
                .shortCode(url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .shortUrl(shortUrl)
                .customAlias(url.getCustomAlias())
                .title(url.getTitle())
                .clickCount(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .lastAccessedAt(url.getLastAccessedAt())
                .active(url.getActive())
                .hasPassword(url.getPassword() != null && !url.getPassword().isEmpty())
                .qrCodeBase64(qrCodeService.generateQrCode(shortUrl))
                .build();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private List<AnalyticsDto.DailyClick> getClicksOverTime(Long urlId) {
        List<Object[]> results = clickEventRepository.getDailyClicks(urlId);
        return results.stream()
                .map(r -> AnalyticsDto.DailyClick.builder()
                        .date((LocalDate) r[0])
                        .count((Long) r[1])
                        .build())
                .collect(Collectors.toList());
    }

    private List<AnalyticsDto.BrowserStat> getBrowserStats(Long urlId) {
        List<Object[]> results = clickEventRepository.getBrowserStats(urlId);
        return mapToStatsList(results, AnalyticsDto.BrowserStat.class);
    }

    private List<AnalyticsDto.OsStat> getOsStats(Long urlId) {
        List<Object[]> results = clickEventRepository.getOsStats(urlId);
        return mapToStatsList(results, AnalyticsDto.OsStat.class);
    }

    private List<AnalyticsDto.DeviceStat> getDeviceStats(Long urlId) {
        List<Object[]> results = clickEventRepository.getDeviceStats(urlId);
        return mapToStatsList(results, AnalyticsDto.DeviceStat.class);
    }

    private List<AnalyticsDto.ReferrerStat> getReferrerStats(Long urlId) {
        List<Object[]> results = clickEventRepository.getReferrerStats(urlId);
        return mapToStatsList(results, AnalyticsDto.ReferrerStat.class);
    }

    private List<AnalyticsDto.CountryStat> getCountryStats(Long urlId) {
        List<Object[]> results = clickEventRepository.getCountryStats(urlId);
        return mapToStatsList(results, AnalyticsDto.CountryStat.class);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> mapToStatsList(List<Object[]> results, Class<T> clazz) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }

        long total = results.stream().mapToLong(r -> (Long) r[1]).sum();

        return results.stream().map(r -> {
            String name = r[0] != null ? r[0].toString() : "Unknown";
            Long count = (Long) r[1];
            Double percentage = total > 0 ? Math.round((count * 100.0 / total) * 100.0) / 100.0 : 0.0;

            if (clazz == AnalyticsDto.BrowserStat.class) {
                return (T) AnalyticsDto.BrowserStat.builder().name(name).count(count).percentage(percentage).build();
            } else if (clazz == AnalyticsDto.OsStat.class) {
                return (T) AnalyticsDto.OsStat.builder().name(name).count(count).percentage(percentage).build();
            } else if (clazz == AnalyticsDto.DeviceStat.class) {
                return (T) AnalyticsDto.DeviceStat.builder().name(name).count(count).percentage(percentage).build();
            } else if (clazz == AnalyticsDto.ReferrerStat.class) {
                return (T) AnalyticsDto.ReferrerStat.builder().name(name).count(count).percentage(percentage).build();
            } else if (clazz == AnalyticsDto.CountryStat.class) {
                return (T) AnalyticsDto.CountryStat.builder().name(name).count(count).percentage(percentage).build();
            }
            return null;
        }).collect(Collectors.toList());
    }

    // ==================== Scheduled Tasks ====================

    /**
     * Clean up expired URLs - runs every hour
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredUrls() {
        log.info("Running expired URL cleanup task");
        int deactivated = urlRepository.deactivateExpiredUrls(LocalDateTime.now());
        if (deactivated > 0) {
            log.info("Deactivated {} expired URLs", deactivated);
        }
    }

    // ==================== Inner Classes ====================

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DashboardStats {
        private long totalUrls;
        private long activeUrls;
        private Long totalClicks;
        private List<UrlResponse> recentUrls;
    }
}
