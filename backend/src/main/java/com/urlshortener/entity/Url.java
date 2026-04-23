package com.urlshortener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * URL Entity - Stores the mapping between short codes and original URLs
 */
@Entity
@Table(name = "urls", indexes = {
    @Index(name = "idx_short_code", columnList = "shortCode", unique = true),
    @Index(name = "idx_original_url", columnList = "originalUrl"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 50)
    private String shortCode;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "custom_alias", length = 50)
    private String customAlias;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "click_count")
    @Builder.Default
    private Long clickCount = 0L;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "password", length = 255)
    private String password;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public void incrementClickCount() {
        this.clickCount = this.clickCount + 1;
    }
}
