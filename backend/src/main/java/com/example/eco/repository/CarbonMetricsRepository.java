package com.example.eco.repository;

import com.example.eco.model.CarbonMetrics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarbonMetricsRepository extends JpaRepository<CarbonMetrics, Long> {
    
    // Récupérer l'historique avec pagination
    Page<CarbonMetrics> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);
    
    // Historique avec filtres de date
    @Query("SELECT c FROM CarbonMetrics c WHERE c.userId = :userId " +
           "AND c.date BETWEEN :startDate AND :endDate " +
           "ORDER BY c.date DESC")
    List<CarbonMetrics> findByUserIdAndDateBetween(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Statistiques agrégées
    @Query("SELECT SUM(c.sessionCarbon) FROM CarbonMetrics c " +
           "WHERE c.userId = :userId AND c.date >= :startDate")
    Double getTotalCarbonSince(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate
    );
    
    // Moyenne par période
    @Query("SELECT AVG(c.sessionCarbon) FROM CarbonMetrics c " +
           "WHERE c.userId = :userId AND c.date BETWEEN :startDate AND :endDate")
    Double getAverageCarbonBetween(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}