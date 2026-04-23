package com.urlshortener.controller;

import com.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for URL redirection
 * Handles the actual short URL to long URL redirection
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final UrlService urlService;

    /**
     * Redirect short URL to original URL
     * GET /{shortCode}
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(
            @PathVariable String shortCode,
            HttpServletRequest request) {
        try {
            String originalUrl = urlService.getOriginalUrlAndTrack(shortCode, request);
            
            // Ensure URL has protocol
            if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
                originalUrl = "https://" + originalUrl;
            }
            
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        } catch (RuntimeException e) {
            log.error("Redirection error for {}: {}", shortCode, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get redirect URL info (without actual redirect)
     * GET /api/redirect/{shortCode}/info
     */
    @GetMapping("/api/redirect/{shortCode}/info")
    public ResponseEntity<Map<String, Object>> getRedirectInfo(@PathVariable String shortCode) {
        try {
            return urlService.getUrlByShortCode(shortCode)
                    .map(url -> {
                        Map<String, Object> info = new HashMap<>();
                        info.put("originalUrl", url.getOriginalUrl());
                        info.put("shortCode", url.getShortCode());
                        info.put("title", url.getTitle());
                        info.put("clickCount", url.getClickCount());
                        info.put("active", url.getActive());
                        info.put("expired", url.getExpiresAt() != null && 
                                java.time.LocalDateTime.now().isAfter(url.getExpiresAt()));
                        return ResponseEntity.ok(info);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting redirect info: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
