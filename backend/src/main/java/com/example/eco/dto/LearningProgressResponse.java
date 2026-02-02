package com.example.eco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgressResponse {
    
    private Long userId;
    private List<PathProgress> pathsProgress;
    private ProgressStatistics statistics;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PathProgress {
        private Long pathId;
        private String title;
        private String difficulty;
        private Integer completionPercentage;
        private Integer modulesCompleted;
        private Integer totalModules;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressStatistics {
        private Integer totalPaths;
        private Integer completedPaths;
        private Integer inProgressPaths;
        private Double averageCompletionRate;
        private String mostStudiedTopic;
    }
}