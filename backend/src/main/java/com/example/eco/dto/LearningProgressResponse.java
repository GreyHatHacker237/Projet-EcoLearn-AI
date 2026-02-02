package com.example.eco.dto;

import java.util.List;

public class LearningProgressResponse {
    private Long userId;
    private List<PathProgress> paths;
    private ProgressStatistics statistics;

    // Getters
    public Long getUserId() { return userId; }
    public List<PathProgress> getPaths() { return paths; }
    public ProgressStatistics getStatistics() { return statistics; }

    // Setters
    public void setUserId(Long userId) { this.userId = userId; }
    public void setPaths(List<PathProgress> paths) { this.paths = paths; }
    public void setStatistics(ProgressStatistics statistics) { this.statistics = statistics; }

    public static class PathProgress {
        private Long pathId;
        private String title;
        private Integer progressPercentage;

        public PathProgress() {}
        
        public PathProgress(Long pathId, String title, Integer progressPercentage) {
            this.pathId = pathId;
            this.title = title;
            this.progressPercentage = progressPercentage;
        }

        // Getters/Setters
        public Long getPathId() { return pathId; }
        public void setPathId(Long pathId) { this.pathId = pathId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getProgressPercentage() { return progressPercentage; }
        public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    }

    public static class ProgressStatistics {
        private Integer totalPaths;
        private Integer completedPaths;
        private Integer inProgressPaths;
        private Double averageProgress;
        private String overallStatus;

        public ProgressStatistics() {}
        
        // Getters/Setters
        public Integer getTotalPaths() { return totalPaths; }
        public void setTotalPaths(Integer totalPaths) { this.totalPaths = totalPaths; }
        public Integer getCompletedPaths() { return completedPaths; }
        public void setCompletedPaths(Integer completedPaths) { this.completedPaths = completedPaths; }
        public Integer getInProgressPaths() { return inProgressPaths; }
        public void setInProgressPaths(Integer inProgressPaths) { this.inProgressPaths = inProgressPaths; }
        public Double getAverageProgress() { return averageProgress; }
        public void setAverageProgress(Double averageProgress) { this.averageProgress = averageProgress; }
        public String getOverallStatus() { return overallStatus; }
        public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }
    }
}