package com.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO for URL analytics data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDto {

    private Long urlId;
    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private Long uniqueVisitors;
    private List<DailyClick> clicksOverTime;
    private List<BrowserStat> browsers;
    private List<OsStat> operatingSystems;
    private List<DeviceStat> devices;
    private List<ReferrerStat> referrers;
    private List<CountryStat> countries;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyClick {
        private LocalDate date;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrowserStat {
        private String name;
        private Long count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OsStat {
        private String name;
        private Long count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceStat {
        private String name;
        private Long count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferrerStat {
        private String name;
        private Long count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryStat {
        private String name;
        private Long count;
        private Double percentage;
    }
}
