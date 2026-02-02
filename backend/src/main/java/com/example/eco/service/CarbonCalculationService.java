package com.example.eco.service;

import com.example.eco.model.CarbonMetrics;
import com.example.eco.model.User;
import com.example.eco.repository.CarbonMetricsRepository;
import com.example.eco.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonCalculationService {
    
    private final CarbonMetricsRepository carbonMetricsRepository;
    private final UserRepository userRepository;
    
    // Constantes pour le calcul carbone
    private static final double CARBON_PER_HOUR_STREAMING = 0.05; // kg CO2/heure streaming
    private static final double CARBON_PER_MB_DATA = 0.0000004; // kg CO2/MB
    private static final double CARBON_PER_REQUEST = 0.000001; // kg CO2/requête API
    private static final double TREE_CARBON_OFFSET = 21.77; // kg CO2/arbre/an
    
    /**
     * Calcule et enregistre l'empreinte carbone d'une session
     */
    @Transactional
    public CarbonMetrics calculateAndSaveSessionCarbon(Long userId, SessionData sessionData) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        double sessionCarbon = calculateSessionCarbon(sessionData);
        double totalCarbon = getUserTotalCarbon(userId) + sessionCarbon;
        int treesNeeded = calculateTreesNeeded(totalCarbon);
        
        CarbonMetrics metrics = CarbonMetrics.builder()
            .user(user)
            .sessionDuration(sessionData.getDurationHours())
            .dataUsedMB(sessionData.getDataUsedMB())
            .apiRequests(sessionData.getApiRequests())
            .deviceType(sessionData.getDeviceType())
            .energySource(sessionData.getEnergySource())
            .sessionCarbon(sessionCarbon)
            .totalCarbon(totalCarbon)
            .treesNeeded(treesNeeded)
            .date(LocalDate.now())
            .createdAt(LocalDateTime.now())
            .build();
        
        carbonMetricsRepository.save(metrics);
        log.info("Empreinte carbone calculée: {} kg CO2 pour la session", sessionCarbon);
        
        return metrics;
    }
    
    /**
     * Calcule l'empreinte carbone d'une session
     */
    public double calculateSessionCarbon(SessionData sessionData) {
        double carbon = 0.0;
        
        // Calcul basé sur la durée (streaming équivalent)
        carbon += sessionData.getDurationHours() * CARBON_PER_HOUR_STREAMING;
        
        // Calcul basé sur les données utilisées
        carbon += sessionData.getDataUsedMB() * CARBON_PER_MB_DATA;
        
        // Calcul basé sur les requêtes API
        carbon += sessionData.getApiRequests() * CARBON_PER_REQUEST;
        
        // Facteur d'appareil
        carbon *= getDeviceFactor(sessionData.getDeviceType());
        
        // Facteur de source d'énergie
        carbon *= getEnergyFactor(sessionData.getEnergySource());
        
        return Math.round(carbon * 1000.0) / 1000.0; // Arrondi à 3 décimales
    }
    
    /**
     * Calcule le nombre d'arbres nécessaires pour compenser
     */
    public int calculateTreesNeeded(double totalCarbonKg) {
        // Un arbre absorbe environ 21.77 kg CO2 par an
        return (int) Math.ceil(totalCarbonKg / TREE_CARBON_OFFSET);
    }
    
    /**
     * Récupère les statistiques carbone d'un utilisateur
     */
    public CarbonStats getUserCarbonStats(Long userId) {
        List<CarbonMetrics> userMetrics = carbonMetricsRepository.findByUserId(userId);
        
        if (userMetrics.isEmpty()) {
            return CarbonStats.empty();
        }
        
        DoubleSummaryStatistics sessionStats = userMetrics.stream()
            .mapToDouble(CarbonMetrics::getSessionCarbon)
            .summaryStatistics();
        
        double totalCarbonOffset = userMetrics.stream()
            .mapToDouble(m -> m.getTreesNeeded() * TREE_CARBON_OFFSET)
            .sum();
        
        int totalTreesNeeded = userMetrics.stream()
            .mapToInt(CarbonMetrics::getTreesNeeded)
            .sum();
        
        return CarbonStats.builder()
            .totalSessions(userMetrics.size())
            .totalCarbon(sessionStats.getSum())
            .averageSessionCarbon(sessionStats.getAverage())
            .maxSessionCarbon(sessionStats.getMax())
            .minSessionCarbon(sessionStats.getMin())
            .totalCarbonOffset(totalCarbonOffset)
            .totalTreesNeeded(totalTreesNeeded)
            .carbonPerDay(calculateCarbonPerDay(userMetrics))
            .build();
    }
    
    /**
     * Calcule la moyenne de carbone par jour
     */
    private List<CarbonPerDay> calculateCarbonPerDay(List<CarbonMetrics> metrics) {
        return metrics.stream()
            .collect(Collectors.groupingBy(
                CarbonMetrics::getDate,
                Collectors.summingDouble(CarbonMetrics::getSessionCarbon)
            ))
            .entrySet().stream()
            .map(entry -> new CarbonPerDay(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
    
    /**
     * Facteur selon le type d'appareil
     */
    private double getDeviceFactor(String deviceType) {
        return switch (deviceType.toLowerCase()) {
            case "mobile" -> 0.8;
            case "tablet" -> 0.9;
            case "laptop" -> 1.0;
            case "desktop" -> 1.2;
            default -> 1.0;
        };
    }
    
    /**
     * Facteur selon la source d'énergie
     */
    private double getEnergyFactor(String energySource) {
        return switch (energySource.toLowerCase()) {
            case "renewable" -> 0.3;
            case "mixed" -> 0.7;
            case "fossil" -> 1.0;
            default -> 0.7; // Par défaut mixte
        };
    }
    
    /**
     * Récupère le total carbone d'un utilisateur
     */
    private double getUserTotalCarbon(Long userId) {
        return carbonMetricsRepository.findByUserId(userId).stream()
            .mapToDouble(CarbonMetrics::getSessionCarbon)
            .sum();
    }
    
    // Classes internes pour les données
    @Data
    @Builder
    public static class SessionData {
        private double durationHours;
        private double dataUsedMB;
        private int apiRequests;
        private String deviceType; // mobile, tablet, laptop, desktop
        private String energySource; // renewable, mixed, fossil
    }
    
    @Data
    @Builder
    public static class CarbonStats {
        private int totalSessions;
        private double totalCarbon;
        private double averageSessionCarbon;
        private double maxSessionCarbon;
        private double minSessionCarbon;
        private double totalCarbonOffset;
        private int totalTreesNeeded;
        private List<CarbonPerDay> carbonPerDay;
        
        public static CarbonStats empty() {
            return CarbonStats.builder()
                .totalSessions(0)
                .totalCarbon(0)
                .averageSessionCarbon(0)
                .maxSessionCarbon(0)
                .minSessionCarbon(0)
                .totalCarbonOffset(0)
                .totalTreesNeeded(0)
                .carbonPerDay(List.of())
                .build();
        }
    }
    
    @Data
    @AllArgsConstructor
    public static class CarbonPerDay {
        private LocalDate date;
        private double carbon;
    }
}