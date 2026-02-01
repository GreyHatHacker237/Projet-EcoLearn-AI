package com.project.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "carbon_metrics", indexes = {
    @Index(name = "idx_carbon_user_id", columnList = "user_id"),
    @Index(name = "idx_carbon_date", columnList = "date"),
    @Index(name = "idx_carbon_user_date", columnList = "user_id, date")
})
@Data
public class CarbonMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "session_carbon", nullable = false)
    private Double sessionCarbon;
    
    @Column(name = "total_carbon", nullable = false)
    private Double totalCarbon;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}