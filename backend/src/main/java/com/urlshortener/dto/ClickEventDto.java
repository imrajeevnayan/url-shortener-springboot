package com.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for click event data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickEventDto {

    private Long id;
    private Long urlId;
    private String ipAddress;
    private String browser;
    private String os;
    private String device;
    private String country;
    private String referer;
    private LocalDateTime clickedAt;
}
