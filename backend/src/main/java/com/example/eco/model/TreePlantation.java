package com.example.eco.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tree_plantations")
public class TreePlantation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private Integer treesPlanted;
    private Double carbonOffset;
    private LocalDate plantedAt;

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Integer getTreesPlanted() { return treesPlanted; }
    public Double getCarbonOffset() { return carbonOffset; }
    public LocalDate getPlantedAt() { return plantedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTreesPlanted(Integer treesPlanted) { this.treesPlanted = treesPlanted; }
    public void setCarbonOffset(Double carbonOffset) { this.carbonOffset = carbonOffset; }
    public void setPlantedAt(LocalDate plantedAt) { this.plantedAt = plantedAt; }
}