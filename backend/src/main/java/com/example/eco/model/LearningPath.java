package com.example.eco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "learning_paths",
    indexes = {
        @Index(name = "idx_learning_user_id", columnList = "user_id"),
        @Index(name = "idx_learning_difficulty", columnList = "difficulty"),
        @Index(name = "idx_learning_user_difficulty", columnList = "user_id, difficulty")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
