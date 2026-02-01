package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonHistoryResponse {
    
    private Long userId;
    private List<CarbonDataPoint> history;
    private CarbonStatistics statistics;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarbonDataPoint {
        private LocalDate date;
        private Double sessionCarbon;
        private Double totalCarbon;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarbonStatistics {
        private Double totalCarbon;
        private Double averagePerSession;
        private Integer totalSessions;
        private LocalDate firstSessionDate;
        private LocalDate lastSessionDate;
    }
}