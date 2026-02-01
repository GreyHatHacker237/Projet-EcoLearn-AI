package com.project.service;

import com.project.dto.CarbonHistoryResponse;
import com.project.dto.LearningProgressResponse;
import com.project.dto.TreePlantationResponse;
import com.project.model.CarbonMetrics;
import com.project.model.LearningPath;
import com.project.model.TreePlantation;
import com.project.repository.CarbonMetricsRepository;
import com.project.repository.LearningPathRepository;
import com.project.repository.TreePlantationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final CarbonMetricsRepository carbonMetricsRepository;
    private final TreePlantationRepository treePlantationRepository;
    private final LearningPathRepository learningPathRepository;

    /**
     * Historique de l'empreinte carbone avec pagination et filtres
     */
    @Cacheable(value = "carbonHistory", key = "#userId + '-' + #page + '-' + #size")
    public CarbonHistoryResponse getCarbonHistory(
            Long userId, 
            LocalDate startDate, 
            LocalDate endDate,
            int page,
            int size) {
        
        // Si pas de dates, prendre les 30 derniers jours
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // Récupérer les données avec filtres
        List<CarbonMetrics> metrics = carbonMetricsRepository
            .findByUserIdAndDateBetween(userId, startDate, endDate);
        
        // Convertir en points de données
        List<CarbonHistoryResponse.CarbonDataPoint> dataPoints = metrics.stream()
            .map(m -> new CarbonHistoryResponse.CarbonDataPoint(
                m.getDate(),
                m.getSessionCarbon(),
                m.getTotalCarbon()
            ))
            .collect(Collectors.toList());
        
        // Calculer les statistiques
        CarbonHistoryResponse.CarbonStatistics stats = calculateCarbonStatistics(userId, metrics);
        
        return new CarbonHistoryResponse(userId, dataPoints, stats);
    }

    /**
     * Évolution des plantations d'arbres
     */
    @Cacheable(value = "treesPlanted", key = "#userId + '-' + #months")
    public TreePlantationResponse getTreesPlantedEvolution(Long userId, int months) {
        
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        
        List<TreePlantation> plantations = treePlantationRepository
            .findEvolutionSince(userId, startDate);
        
        // Calculer les arbres cumulés
        int cumulativeTrees = 0;
        List<TreePlantationResponse.PlantationDataPoint> dataPoints = new ArrayList<>();
        
        for (TreePlantation plantation : plantations) {
            cumulativeTrees += plantation.getTreesPlanted();
            dataPoints.add(new TreePlantationResponse.PlantationDataPoint(
                plantation.getPlantedAt(),
                plantation.getTreesPlanted(),
                plantation.getCarbonOffset(),
                cumulativeTrees
            ));
        }
        
        // Statistiques
        TreePlantationResponse.PlantationStatistics stats = calculatePlantationStatistics(userId, plantations);
        
        return new TreePlantationResponse(userId, dataPoints, stats);
    }

    /**
     * Progression de l'apprentissage
     */
    @Cacheable(value = "learningProgress", key = "#userId")
    public LearningProgressResponse getLearningProgress(Long userId) {
        
        List<LearningPath> paths = learningPathRepository.findByUserId(userId);
        
        // Calculer la progression pour chaque parcours
        List<LearningProgressResponse.PathProgress> pathsProgress = paths.stream()
            .map(this::calculatePathProgress)
            .collect(Collectors.toList());
        
        // Statistiques globales
        LearningProgressResponse.ProgressStatistics stats = calculateProgressStatistics(pathsProgress);
        
        return new LearningProgressResponse(userId, pathsProgress, stats);
    }

    // ========== MÉTHODES PRIVÉES ==========

    private CarbonHistoryResponse.CarbonStatistics calculateCarbonStatistics(
            Long userId, 
            List<CarbonMetrics> metrics) {
        
        if (metrics.isEmpty()) {
            return new CarbonHistoryResponse.CarbonStatistics(0.0, 0.0, 0, null, null);
        }
        
        Double totalCarbon = metrics.stream()
            .mapToDouble(CarbonMetrics::getSessionCarbon)
            .sum();
        
        Double averagePerSession = totalCarbon / metrics.size();
        
        LocalDate firstDate = metrics.stream()
            .map(CarbonMetrics::getDate)
            .min(LocalDate::compareTo)
            .orElse(null);
        
        LocalDate lastDate = metrics.stream()
            .map(CarbonMetrics::getDate)
            .max(LocalDate::compareTo)
            .orElse(null);
        
        return new CarbonHistoryResponse.CarbonStatistics(
            totalCarbon,
            averagePerSession,
            metrics.size(),
            firstDate,
            lastDate
        );
    }

    private TreePlantationResponse.PlantationStatistics calculatePlantationStatistics(
            Long userId, 
            List<TreePlantation> plantations) {
        
        if (plantations.isEmpty()) {
            return new TreePlantationResponse.PlantationStatistics(0, 0.0, 0, null, null);
        }
        
        Integer totalTrees = treePlantationRepository.getTotalTreesPlanted(userId);
        Double totalOffset = treePlantationRepository.getTotalCarbonOffset(userId);
        
        LocalDateTime first = plantations.stream()
            .map(TreePlantation::getPlantedAt)
            .min(LocalDateTime::compareTo)
            .orElse(null);
        
        LocalDateTime last = plantations.stream()
            .map(TreePlantation::getPlantedAt)
            .max(LocalDateTime::compareTo)
            .orElse(null);
        
        return new TreePlantationResponse.PlantationStatistics(
            totalTrees != null ? totalTrees : 0,
            totalOffset != null ? totalOffset : 0.0,
            plantations.size(),
            first,
            last
        );
    }

    private LearningProgressResponse.PathProgress calculatePathProgress(LearningPath path) {
        // Simulation du calcul de progression (à adapter selon votre logique)
        // Ici on suppose que le contenu JSON contient des modules
        int totalModules = 5; // À extraire du contenu JSON
        int completedModules = (int) (Math.random() * totalModules); // À calculer réellement
        int completionPercentage = (completedModules * 100) / totalModules;
        
        return new LearningProgressResponse.PathProgress(
            path.getId(),
            path.getTitle(),
            path.getDifficulty(),
            completionPercentage,
            completedModules,
            totalModules
        );
    }

    private LearningProgressResponse.ProgressStatistics calculateProgressStatistics(
            List<LearningProgressResponse.PathProgress> pathsProgress) {
        
        if (pathsProgress.isEmpty()) {
            return new LearningProgressResponse.ProgressStatistics(0, 0, 0, 0.0, null);
        }
        
        int totalPaths = pathsProgress.size();
        int completedPaths = (int) pathsProgress.stream()
            .filter(p -> p.getCompletionPercentage() == 100)
            .count();
        int inProgressPaths = totalPaths - completedPaths;
        
        double averageCompletion = pathsProgress.stream()
            .mapToInt(LearningProgressResponse.PathProgress::getCompletionPercentage)
            .average()
            .orElse(0.0);
        
        // Trouver le sujet le plus étudié (basé sur le titre du parcours)
        String mostStudied = pathsProgress.stream()
            .findFirst()
            .map(LearningProgressResponse.PathProgress::getTitle)
            .orElse("Aucun");
        
        return new LearningProgressResponse.ProgressStatistics(
            totalPaths,
            completedPaths,
            inProgressPaths,
            averageCompletion,
            mostStudied
        );
    }

    /**
     * Historique carbone avec pagination complète
     */
    public PageResponse<CarbonHistoryResponse.CarbonDataPoint> getCarbonHistoryPaginated(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // Pagination avec Spring Data
        Pageable pageable = PageRequest.of(page, size);
        Page<CarbonMetrics> metricsPage = carbonMetricsRepository
            .findByUserIdOrderByDateDesc(userId, pageable);
        
        // Conversion
        List<CarbonHistoryResponse.CarbonDataPoint> dataPoints = metricsPage.getContent().stream()
            .map(m -> new CarbonHistoryResponse.CarbonDataPoint(
                m.getDate(),
                m.getSessionCarbon(),
                m.getTotalCarbon()
            ))
            .collect(Collectors.toList());
        
        return PageResponse.of(
            dataPoints,
            metricsPage.getNumber(),
            metricsPage.getSize(),
            metricsPage.getTotalElements()
        );
    }
}