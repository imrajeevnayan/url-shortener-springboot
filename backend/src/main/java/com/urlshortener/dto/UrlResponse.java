package com.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for URL response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponse {

    private Long id;
    private String shortCode;
    private String originalUrl;
    private String shortUrl;
    private String customAlias;
    private String title;
    private String description;
    private Long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastAccessedAt;
    private Boolean active;
    private Boolean hasPassword;
    private String qrCodeBase64;
}
