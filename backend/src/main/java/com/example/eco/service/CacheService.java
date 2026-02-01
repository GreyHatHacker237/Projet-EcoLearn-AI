package com.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    /**
     * Invalide le cache pour un utilisateur spécifique
     */
    public void evictUserCache(Long userId) {
        Objects.requireNonNull(cacheManager.getCache("carbonHistory")).clear();
        Objects.requireNonNull(cacheManager.getCache("treesPlanted")).clear();
        Objects.requireNonNull(cacheManager.getCache("learningProgress")).clear();
    }

    /**
     * Invalide tout le cache
     */
    public void evictAllCaches() {
        cacheManager.getCacheNames()
            .forEach(cacheName -> 
                Objects.requireNonNull(cacheManager.getCache(cacheName)).clear()
            );
    }
}