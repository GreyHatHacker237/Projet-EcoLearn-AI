package com.project.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LearningPathResponse {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String difficulty;
    private LocalDateTime createdAt;
}