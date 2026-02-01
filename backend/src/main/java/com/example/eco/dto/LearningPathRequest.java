package com.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LearningPathRequest {
    
    @NotBlank(message = "Le sujet est obligatoire")
    private String topic;
    
    @NotBlank(message = "La difficulté est obligatoire")
    private String difficulty; // "débutant", "intermédiaire", "avancé"
    
    private Long userId;
}