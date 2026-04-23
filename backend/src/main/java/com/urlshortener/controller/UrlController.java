package com.urlshortener.controller;

import com.urlshortener.dto.*;
import com.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for URL Shortener API
 * Provides endpoints for CRUD operations, analytics, and management
 */
@Slf4j
@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UrlController {

    private final UrlService urlService;

    /**
     * Create a new shortened URL
     * POST /api/urls
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UrlResponse>> createShortUrl(@Valid @RequestBody UrlRequest request) {
        try {
            UrlResponse response = urlService.createShortUrl(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("URL shortened successfully", response));
        } catch (RuntimeException e) {
            log.error("Error creating short URL: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get all URLs with pagination
     * GET /api/urls?page=0&size=10&sortBy=createdAt&direction=desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UrlResponse>>> getAllUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Page<UrlResponse> urls = urlService.getAllUrls(page, size, sortBy, direction);
            return ResponseEntity.ok(ApiResponse.success(urls));
        } catch (Exception e) {
            log.error("Error fetching URLs: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch URLs"));
        }
    }

    /**
     * Get recently created URLs
     * GET /api/urls/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getRecentUrls() {
        try {
            List<UrlResponse> urls = urlService.getRecentUrls();
            return ResponseEntity.ok(ApiResponse.success(urls));
        } catch (Exception e) {
            log.error("Error fetching recent URLs: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch recent URLs"));
        }
    }

    /**
     * Get URL by short code
     * GET /api/urls/{shortCode}
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<ApiResponse<UrlResponse>> getUrlByShortCode(@PathVariable String shortCode) {
        try {
            return urlService.getUrlByShortCode(shortCode)
                    .map(url -> ResponseEntity.ok(ApiResponse.success(url)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching URL: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch URL"));
        }
    }

    /**
     * Search URLs by keyword
     * GET /api/urls/search?keyword=example
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UrlResponse>>> searchUrls(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UrlResponse> urls = urlService.searchUrls(keyword, page, size);
            return ResponseEntity.ok(ApiResponse.success(urls));
        } catch (Exception e) {
            log.error("Error searching URLs: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to search URLs"));
        }
    }

    /**
     * Update URL
     * PUT /api/urls/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UrlResponse>> updateUrl(
            @PathVariable Long id,
            @Valid @RequestBody UrlRequest request) {
        try {
            UrlResponse response = urlService.updateUrl(id, request);
            return ResponseEntity.ok(ApiResponse.success("URL updated successfully", response));
        } catch (RuntimeException e) {
            log.error("Error updating URL: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Delete (deactivate) URL
     * DELETE /api/urls/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUrl(@PathVariable Long id) {
        try {
            urlService.deleteUrl(id);
            return ResponseEntity.ok(ApiResponse.success("URL deleted successfully", null));
        } catch (RuntimeException e) {
            log.error("Error deleting URL: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Permanently delete URL and analytics
     * DELETE /api/urls/{id}/permanent
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentDeleteUrl(@PathVariable Long id) {
        try {
            urlService.permanentDeleteUrl(id);
            return ResponseEntity.ok(ApiResponse.success("URL permanently deleted", null));
        } catch (RuntimeException e) {
            log.error("Error permanently deleting URL: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get analytics for a URL
     * GET /api/urls/{id}/analytics
     */
    @GetMapping("/{id}/analytics")
    public ResponseEntity<ApiResponse<AnalyticsDto>> getAnalytics(@PathVariable Long id) {
        try {
            AnalyticsDto analytics = urlService.getAnalytics(id);
            return ResponseEntity.ok(ApiResponse.success(analytics));
        } catch (RuntimeException e) {
            log.error("Error fetching analytics: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Validate short code availability
     * GET /api/urls/check/{shortCode}
     */
    @GetMapping("/check/{shortCode}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkShortCode(@PathVariable String shortCode) {
        try {
            boolean exists = urlService.getUrlByShortCode(shortCode).isPresent();
            Map<String, Object> result = new HashMap<>();
            result.put("available", !exists);
            result.put("shortCode", shortCode);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("Error checking short code: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to check short code"));
        }
    }
}
