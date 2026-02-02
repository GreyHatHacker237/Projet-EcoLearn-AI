package com.example.eco.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "carbon_metrics")
public class CarbonMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private Double sessionCarbon;
    private Double totalCarbon;
    private LocalDate date;

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Double getSessionCarbon() { return sessionCarbon; }
    public Double getTotalCarbon() { return totalCarbon; }
    public LocalDate getDate() { return date; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setSessionCarbon(Double sessionCarbon) { this.sessionCarbon = sessionCarbon; }
    public void setTotalCarbon(Double totalCarbon) { this.totalCarbon = totalCarbon; }
    public void setDate(LocalDate date) { this.date = date; }
}