package com.example.eco.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private CacheManager cacheManager;

    public void clearAllCaches() {
        cacheManager.getCacheNames()
            .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }
}