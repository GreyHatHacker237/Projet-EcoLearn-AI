package com.example.eco.service;

import com.example.eco.dto.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatsService {
    
    public CarbonHistoryResponse getCarbonHistory(Long userId, LocalDate startDate, LocalDate endDate, int page, int size) {
        CarbonHistoryResponse response = new CarbonHistoryResponse();
        response.setUserId(userId);
        response.setData(List.of());
        response.setStatistics(new CarbonHistoryResponse.CarbonStatistics());
        return response;
    }

    public TreePlantationResponse getTreesPlantedEvolution(Long userId, int months) {
        TreePlantationResponse response = new TreePlantationResponse();
        response.setUserId(userId);
        response.setData(List.of());
        response.setStatistics(new TreePlantationResponse.PlantationStatistics());
        return response;
    }

    public LearningProgressResponse getLearningProgress(Long userId) {
        LearningProgressResponse response = new LearningProgressResponse();
        response.setUserId(userId);
        response.setPaths(List.of());
        response.setStatistics(new LearningProgressResponse.ProgressStatistics());
        return response;
    }

    public CarbonHistoryResponse getCarbonHistoryPaginated(Long userId, LocalDate startDate, LocalDate endDate, int page, int size) {
        return getCarbonHistory(userId, startDate, endDate, page, size);
    }
}