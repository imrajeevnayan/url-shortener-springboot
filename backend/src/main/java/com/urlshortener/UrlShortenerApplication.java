package com.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * URL Shortener Application - Main Entry Point
 * A full-featured URL shortening service with analytics, QR codes,
 * custom aliases, and expiration management.
 */
@SpringBootApplication
@EnableScheduling
public class UrlShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlShortenerApplication.class, args);
    }
}
