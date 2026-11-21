package com.lazar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lazar")
public record LazarProperties(Jwt jwt, Ai ai, Usage usage) {
    public record Jwt(String secret, long expirationMs) {}
    public record Ai(String apiKey, String model) {}
    public record Usage(int freeDailyLimit) {}
}
