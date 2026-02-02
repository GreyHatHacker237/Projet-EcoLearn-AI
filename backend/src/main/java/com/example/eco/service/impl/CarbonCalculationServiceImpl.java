package com.example.eco.service.impl;

import com.example.eco.model.CarbonMetrics;
import com.example.eco.model.User;
import com.example.eco.repository.CarbonMetricsRepository;
import com.example.eco.repository.UserRepository;
import com.example.eco.service.CarbonCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonCalculationServiceImpl implements CarbonCalculationService {
    
    private final CarbonMetricsRepository carbonMetricsRepository;
    private final UserRepository userRepository;
    
    // Constantes de calcul
    private static final double CARBON_PER_HOUR = 0.05; // kg CO2/heure
    private static final double CARBON_PER_MB = 0.0000004; // kg CO2/MB
    private static final double TREE_OFFSET_PER_KG = 0.046; // arbres par kg CO2
    
    @Override
    public double calculateSessionCarbon(SessionData sessionData) {
        // 1. Calcul base (durée)
        double carbon = sessionData.getDurationHours() * CARBON_PER_HOUR;
        
        // 2. Ajout données consommées
        carbon += sessionData.getDataUsedMB() * CARBON_PER_MB;
        
        // 3. Facteur appareil
        carbon *= getDeviceFactor(sessionData.getDeviceType());
        
        // 4. Facteur énergie
        carbon *= getEnergyFactor(sessionData.getEnergySource());
        
        // Arrondi à 3 décimales
        return Math.round(carbon * 1000.0) / 1000.0;
    }
    
    @Override
    @Transactional
    public CarbonMetrics saveCarbonMetrics(Long userId, SessionData sessionData) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        double sessionCarbon = calculateSessionCarbon(sessionData);
        
        // Calculer le total utilisateur
        double userTotal = carbonMetricsRepository.findByUserId(userId).stream()
            .mapToDouble(CarbonMetrics::getSessionCarbon)
            .sum() + sessionCarbon;
        
        // Calculer arbres nécessaires
        int treesNeeded = (int) Math.ceil(userTotal * TREE_OFFSET_PER_KG);
        
        CarbonMetrics metrics = new CarbonMetrics();
        metrics.setUser(user);
        metrics.setSessionCarbon(sessionCarbon);
        metrics.setTotalCarbon(userTotal);
        metrics.setTreesNeeded(treesNeeded);
        metrics.setActivityType("learning_session");
        metrics.setDate(LocalDate.now());
        metrics.setCreatedAt(LocalDateTime.now());
        metrics.setDeviceType(sessionData.getDeviceType());
        metrics.setEnergySource(sessionData.getEnergySource());
        
        return carbonMetricsRepository.save(metrics);
    }
    
    @Override
    public List<CarbonMetrics> getUserCarbonHistory(Long userId, LocalDate startDate, LocalDate endDate) {
        return carbonMetricsRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
    
    @Override
    public Map<String, Double> getCarbonStatistics(Long userId, String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = calculateStartDate(period, endDate);
        
        List<CarbonMetrics> metrics = getUserCarbonHistory(userId, startDate, endDate);
        
        if (metrics.isEmpty()) {
            return Map.of(
                "total", 0.0,
                "average", 0.0,
                "sessions", 0.0
            );
        }
        
        double total = metrics.stream()
            .mapToDouble(CarbonMetrics::getSessionCarbon)
            .sum();
        
        double average = total / metrics.size();
        
        return Map.of(
            "total", Math.round(total * 100.0) / 100.0,
            "average", Math.round(average * 100.0) / 100.0,
            "sessions", (double) metrics.size(),
            "treesNeeded", (double) metrics.stream()
                .mapToInt(CarbonMetrics::getTreesNeeded)
                .sum()
        );
    }
    
    // Méthodes utilitaires
    private double getDeviceFactor(String deviceType) {
        if (deviceType == null) return 1.0;
        
        return switch (deviceType.toLowerCase()) {
            case "mobile" -> 0.8;
            case "tablet" -> 0.9;
            case "laptop" -> 1.0;
            case "desktop" -> 1.2;
            default -> 1.0;
        };
    }
    
    private double getEnergyFactor(String energySource) {
        if (energySource == null) return 0.7;
        
        return switch (energySource.toLowerCase()) {
            case "renewable" -> 0.3;
            case "mixed" -> 0.7;
            case "fossil" -> 1.0;
            default -> 0.7;
        };
    }
    
    private LocalDate calculateStartDate(String period, LocalDate endDate) {
        return switch (period.toLowerCase()) {
            case "week" -> endDate.minusDays(7);
            case "month" -> endDate.minusMonths(1);
            case "quarter" -> endDate.minusMonths(3);
            case "year" -> endDate.minusYears(1);
            default -> endDate.minusDays(30);
        };
    }
}
