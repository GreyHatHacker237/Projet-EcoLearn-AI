package com.example.eco.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathRequest {
    
    @NotBlank(message = "Le sujet est obligatoire")
    private String topic;
    
    @NotBlank(message = "La difficulté est obligatoire")
    private String difficulty; // "débutant", "intermédiaire", "avancé"
    
    private Long userId;
}