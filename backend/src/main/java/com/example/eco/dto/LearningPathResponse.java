package com.example.eco.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathResponse {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String difficulty;
    private LocalDateTime createdAt;
}