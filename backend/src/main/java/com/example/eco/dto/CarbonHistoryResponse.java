package com.example.eco.dto;

import java.time.LocalDate;
import java.util.List;

public class CarbonHistoryResponse {
    private Long userId;
    private List<CarbonDataPoint> data;
    private CarbonStatistics statistics;

    // Constructeur vide
    public CarbonHistoryResponse() {}
    
    // Constructeur complet
    public CarbonHistoryResponse(Long userId, List<CarbonDataPoint> data, CarbonStatistics statistics) {
        this.userId = userId;
        this.data = data;
        this.statistics = statistics;
    }

    // Getters/Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<CarbonDataPoint> getData() { return data; }
    public void setData(List<CarbonDataPoint> data) { this.data = data; }
    public CarbonStatistics getStatistics() { return statistics; }
    public void setStatistics(CarbonStatistics statistics) { this.statistics = statistics; }

    public static class CarbonDataPoint {
        private LocalDate date;
        private Double sessionCarbon;
        private Double totalCarbon;

        public CarbonDataPoint() {}
        
        public CarbonDataPoint(LocalDate date, Double sessionCarbon, Double totalCarbon) {
            this.date = date;
            this.sessionCarbon = sessionCarbon;
            this.totalCarbon = totalCarbon;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public Double getSessionCarbon() { return sessionCarbon; }
        public void setSessionCarbon(Double sessionCarbon) { this.sessionCarbon = sessionCarbon; }
        public Double getTotalCarbon() { return totalCarbon; }
        public void setTotalCarbon(Double totalCarbon) { this.totalCarbon = totalCarbon; }
    }

    public static class CarbonStatistics {
        private Double totalSessionCarbon;
        private Double averageSessionCarbon;
        private Double maxSessionCarbon;
        private Double minSessionCarbon;
        private LocalDate startDate;
        private LocalDate endDate;

        public CarbonStatistics() {}
        
        public CarbonStatistics(Double totalSessionCarbon, Double averageSessionCarbon, 
                               Double maxSessionCarbon, Double minSessionCarbon, 
                               LocalDate startDate, LocalDate endDate) {
            this.totalSessionCarbon = totalSessionCarbon;
            this.averageSessionCarbon = averageSessionCarbon;
            this.maxSessionCarbon = maxSessionCarbon;
            this.minSessionCarbon = minSessionCarbon;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        // Getters/Setters...
        public Double getTotalSessionCarbon() { return totalSessionCarbon; }
        public void setTotalSessionCarbon(Double totalSessionCarbon) { this.totalSessionCarbon = totalSessionCarbon; }
        public Double getAverageSessionCarbon() { return averageSessionCarbon; }
        public void setAverageSessionCarbon(Double averageSessionCarbon) { this.averageSessionCarbon = averageSessionCarbon; }
        public Double getMaxSessionCarbon() { return maxSessionCarbon; }
        public void setMaxSessionCarbon(Double maxSessionCarbon) { this.maxSessionCarbon = maxSessionCarbon; }
        public Double getMinSessionCarbon() { return minSessionCarbon; }
        public void setMinSessionCarbon(Double minSessionCarbon) { this.minSessionCarbon = minSessionCarbon; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }
}