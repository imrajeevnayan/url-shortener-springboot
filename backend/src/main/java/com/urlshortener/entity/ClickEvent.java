package com.urlshortener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Click Event Entity - Tracks analytics for each URL access
 */
@Entity
@Table(name = "click_events", indexes = {
    @Index(name = "idx_url_id", columnList = "urlId"),
    @Index(name = "idx_clicked_at", columnList = "clickedAt"),
    @Index(name = "idx_url_clicked", columnList = "urlId,clickedAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_id", nullable = false)
    private Long urlId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    @Column(name = "referer", length = 2048)
    private String referer;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "browser", length = 100)
    private String browser;

    @Column(name = "os", length = 100)
    private String os;

    @Column(name = "device", length = 50)
    private String device;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @PrePersist
    protected void onCreate() {
        clickedAt = LocalDateTime.now();
    }
}
