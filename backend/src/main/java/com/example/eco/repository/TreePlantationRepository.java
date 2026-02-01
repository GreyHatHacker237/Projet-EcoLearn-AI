package com.project.repository;

import com.project.model.TreePlantation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TreePlantationRepository extends JpaRepository<TreePlantation, Long> {
    
    // Récupérer les plantations avec pagination
    Page<TreePlantation> findByUserIdOrderByPlantedAtDesc(Long userId, Pageable pageable);
    
    // Évolution dans le temps
    @Query("SELECT t FROM TreePlantation t WHERE t.userId = :userId " +
           "AND t.plantedAt >= :startDate ORDER BY t.plantedAt ASC")
    List<TreePlantation> findEvolutionSince(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate
    );
    
    // Total d'arbres plantés
    @Query("SELECT SUM(t.treesPlanted) FROM TreePlantation t WHERE t.userId = :userId")
    Integer getTotalTreesPlanted(@Param("userId") Long userId);
    
    // Total de carbone compensé
    @Query("SELECT SUM(t.carbonOffset) FROM TreePlantation t WHERE t.userId = :userId")
    Double getTotalCarbonOffset(@Param("userId") Long userId);
}