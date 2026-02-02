package com.example.eco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreePlantationResponse {
    
    private Long userId;
    private List<PlantationDataPoint> evolution;
    private PlantationStatistics statistics;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlantationDataPoint {
        private LocalDateTime plantedAt;
        private Integer treesPlanted;
        private Double carbonOffset;
        private Integer cumulativeTrees;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlantationStatistics {
        private Integer totalTrees;
        private Double totalCarbonOffset;
        private Integer totalPlantations;
        private LocalDateTime firstPlantation;
        private LocalDateTime lastPlantation;
    }
}