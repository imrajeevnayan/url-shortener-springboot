package com.urlshortener.controller;

import com.urlshortener.dto.ApiResponse;
import com.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard Controller - Provides aggregated statistics
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final UrlService urlService;

    /**
     * Get dashboard statistics
     * GET /api/dashboard/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UrlService.DashboardStats>> getDashboardStats() {
        try {
            UrlService.DashboardStats stats = urlService.getDashboardStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("Error fetching dashboard stats: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch dashboard statistics"));
        }
    }
}
