package com.example.eco.dto;

import java.time.LocalDate;
import java.util.List;

public class TreePlantationResponse {
    private Long userId;
    private List<PlantationDataPoint> data;
    private PlantationStatistics statistics;

    // Getters
    public Long getUserId() { return userId; }
    public List<PlantationDataPoint> getData() { return data; }
    public PlantationStatistics getStatistics() { return statistics; }

    // Setters
    public void setUserId(Long userId) { this.userId = userId; }
    public void setData(List<PlantationDataPoint> data) { this.data = data; }
    public void setStatistics(PlantationStatistics statistics) { this.statistics = statistics; }

    public static class PlantationDataPoint {
        private LocalDate date;
        private Integer treesPlanted;
        private Double carbonOffset;

        public PlantationDataPoint() {}
        
        public PlantationDataPoint(LocalDate date, Integer treesPlanted, Double carbonOffset) {
            this.date = date;
            this.treesPlanted = treesPlanted;
            this.carbonOffset = carbonOffset;
        }

        // Getters/Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public Integer getTreesPlanted() { return treesPlanted; }
        public void setTreesPlanted(Integer treesPlanted) { this.treesPlanted = treesPlanted; }
        public Double getCarbonOffset() { return carbonOffset; }
        public void setCarbonOffset(Double carbonOffset) { this.carbonOffset = carbonOffset; }
    }

    public static class PlantationStatistics {
        // Ajoute les attributs nécessaires
        private Integer totalTrees;
        private Double totalCarbonOffset;
        // ...

        public PlantationStatistics() {}
        
        // Getters/Setters
        public Integer getTotalTrees() { return totalTrees; }
        public void setTotalTrees(Integer totalTrees) { this.totalTrees = totalTrees; }
        public Double getTotalCarbonOffset() { return totalCarbonOffset; }
        public void setTotalCarbonOffset(Double totalCarbonOffset) { this.totalCarbonOffset = totalCarbonOffset; }
    }
}