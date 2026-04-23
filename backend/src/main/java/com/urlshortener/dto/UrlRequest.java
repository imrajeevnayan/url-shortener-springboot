package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a shortened URL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {

    @NotBlank(message = "Original URL is required")
    @Size(max = 2048, message = "URL must be less than 2048 characters")
    private String originalUrl;

    @Size(max = 50, message = "Custom alias must be less than 50 characters")
    private String customAlias;

    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    private Integer expiryDays;

    @Size(max = 100, message = "Password must be less than 100 characters")
    private String password;
}
