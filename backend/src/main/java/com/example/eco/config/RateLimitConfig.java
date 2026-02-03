package com.example.eco.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    // Cache local des buckets
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();
    private final Map<String, Bucket> sensitiveBucketCache = new ConcurrentHashMap<>();

    /**
     * Résout un bucket pour les endpoints standards
     */
    public Bucket resolveBucket(String key) {
        return bucketCache.computeIfAbsent(key, k -> createStandardBucket());
    }

    /**
     * Résout un bucket pour les endpoints sensibles (IA)
     */
    public Bucket resolveSensitiveBucket(String key) {
        return sensitiveBucketCache.computeIfAbsent(key, k -> createSensitiveBucket());
    }

    /**
     * Crée un bucket pour les endpoints standards
     * 100 requêtes par minute
     */
    private Bucket createStandardBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Crée un bucket pour les endpoints sensibles (IA)
     * 10 requêtes par minute
     */
    private Bucket createSensitiveBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}