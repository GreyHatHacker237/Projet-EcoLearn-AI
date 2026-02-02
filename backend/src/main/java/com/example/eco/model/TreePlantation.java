package com.example.eco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "tree_plantations",
    indexes = {
        @Index(name = "idx_tree_user_id", columnList = "user_id"),
        @Index(name = "idx_tree_planted_at", columnList = "planted_at"),
        @Index(name = "idx_tree_user_planted", columnList = "user_id, planted_at")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreePlantation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "trees_planted", nullable = false)
    private Integer treesPlanted;

    @Column(name = "carbon_offset", nullable = false)
    private Double carbonOffset;

    @CreationTimestamp
    @Column(name = "planted_at", updatable = false)
    private LocalDateTime plantedAt;
}
