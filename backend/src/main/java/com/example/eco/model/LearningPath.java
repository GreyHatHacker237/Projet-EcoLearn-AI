package com.example.eco.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_paths")
public class LearningPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String title;
    private String content;
    private String difficulty;
    private LocalDateTime createdAt;

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getDifficulty() { return difficulty; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}