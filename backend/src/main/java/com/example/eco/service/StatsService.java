package com.example.eco.service;

import com.example.eco.model.CarbonMetrics;
import com.example.eco.model.TreePlantation;
import com.example.eco.model.LearningPath;
import com.example.eco.dto.CarbonHistoryResponse;
import com.example.eco.dto.TreePlantationResponse;
import com.example.eco.dto.LearningProgressResponse;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsService {

    public CarbonHistoryResponse buildCarbonHistory(Long userId, List<CarbonMetrics> metrics) {
        // Exemple simple
        List<CarbonHistoryResponse.CarbonDataPoint> dataPoints = metrics.stream()
            .map(m -> new CarbonHistoryResponse.CarbonDataPoint(m.getDate(), m.getSessionCarbon()))
            .toList();

        CarbonHistoryResponse.CarbonStatistics stats = new CarbonHistoryResponse.CarbonStatistics(
            metrics.stream().mapToDouble(CarbonMetrics::getSessionCarbon).sum(),
            metrics.stream().mapToDouble(CarbonMetrics::getTotalCarbon).sum(),
            metrics.size(),
            metrics.isEmpty() ? null : metrics.get(0).getDate(),
            metrics.isEmpty() ? null : metrics.get(metrics.size() - 1).getDate()
        );

        return new CarbonHistoryResponse(userId, dataPoints, stats);
    }

    public TreePlantationResponse buildPlantationResponse(Long userId, List<TreePlantation> plantations) {
        List<TreePlantationResponse.PlantationDataPoint> dataPoints = plantations.stream()
            .map(p -> new TreePlantationResponse.PlantationDataPoint(p.getPlantedAt(), p.getTreesPlanted()))
            .toList();

        TreePlantationResponse.PlantationStatistics stats = new TreePlantationResponse.PlantationStatistics(
            plantations.size(),
            plantations.stream().mapToDouble(TreePlantation::getCarbonOffset).sum(),
            plantations.stream().mapToInt(TreePlantation::getTreesPlanted).sum(),
            plantations.isEmpty() ? null : plantations.get(0).getPlantedAt(),
            plantations.isEmpty() ? null : plantations.get(plantations.size() - 1).getPlantedAt()
        );

        return new TreePlantationResponse(userId, dataPoints, stats);
    }

    public LearningProgressResponse buildLearningProgress(Long userId, List<LearningPath> paths) {
        List<LearningProgressResponse.PathProgress> progressList = paths.stream()
            .map(p -> new LearningProgressResponse.PathProgress(
                p.getId(),
                p.getTitle(),
                0, // progression fictive
                100 // progression fictive
            ))
            .toList();

        LearningProgressResponse.ProgressStatistics stats = new LearningProgressResponse.ProgressStatistics(
            paths.size(),
            0, // chemins complétés fictifs
            0, // contenus complétés fictifs
            0.0, // progression moyenne fictive
            ""   // optionnel
        );

        return new LearningProgressResponse(userId, progressList, stats);
    }
}
