package com.urlshortener.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User Agent Parser - Extracts browser, OS, and device information
 */
@Component
public class UserAgentParser {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgentInfo {
        private String browser;
        private String os;
        private String device;
    }

    public AgentInfo parse(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return AgentInfo.builder()
                    .browser("Unknown")
                    .os("Unknown")
                    .device("Unknown")
                    .build();
        }

        return AgentInfo.builder()
                .browser(parseBrowser(userAgent))
                .os(parseOs(userAgent))
                .device(parseDevice(userAgent))
                .build();
    }

    private String parseBrowser(String userAgent) {
        String ua = userAgent.toLowerCase();

        if (ua.contains("edg")) return "Edge";
        if (ua.contains("opr") || ua.contains("opera")) return "Opera";
        if (ua.contains("chrome") && ua.contains("chromium")) return "Chromium";
        if (ua.contains("chrome")) return "Chrome";
        if (ua.contains("safari") && ua.contains("version")) return "Safari";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("msie") || ua.contains("trident")) return "Internet Explorer";
        if (ua.contains("brave")) return "Brave";
        if (ua.contains("vivaldi")) return "Vivaldi";
        if (ua.contains("duckduckgo")) return "DuckDuckGo";

        return "Other";
    }

    private String parseOs(String userAgent) {
        String ua = userAgent.toLowerCase();

        if (ua.contains("windows nt 10")) return "Windows 10/11";
        if (ua.contains("windows nt 6.3")) return "Windows 8.1";
        if (ua.contains("windows nt 6.2")) return "Windows 8";
        if (ua.contains("windows nt 6.1")) return "Windows 7";
        if (ua.contains("windows")) return "Windows";
        if (ua.contains("macintosh") || ua.contains("mac os")) return "macOS";
        if (ua.contains("linux") && ua.contains("android")) return "Android";
        if (ua.contains("android")) return "Android";
        if (ua.contains("linux")) return "Linux";
        if (ua.contains("iphone") || ua.contains("ipad")) return "iOS";
        if (ua.contains("cros")) return "Chrome OS";

        return "Other";
    }

    private String parseDevice(String userAgent) {
        String ua = userAgent.toLowerCase();

        if (ua.contains("mobile")) return "Mobile";
        if (ua.contains("tablet")) return "Tablet";
        if (ua.contains("ipad")) return "Tablet";
        if (ua.contains("iphone")) return "Mobile";
        if (ua.contains("android")) return "Mobile";
        if (ua.contains("smart-tv") || ua.contains("smarttv")) return "Smart TV";

        return "Desktop";
    }
}
