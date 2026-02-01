package com.project.controller;

import com.project.dto.CarbonHistoryResponse;
import com.project.dto.LearningProgressResponse;
import com.project.dto.TreePlantationResponse;
import com.project.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "API de statistiques et analyses")
public class StatsController {

    private final StatsService statsService;

    /**
     * GET /api/stats/carbon-history
     * Récupère l'historique de l'empreinte carbone
     */
    @GetMapping("/carbon-history")
    @Operation(
        summary = "Historique de l'empreinte carbone",
        description = "Retourne l'historique détaillé des émissions carbone avec statistiques agrégées"
    )
    public ResponseEntity<CarbonHistoryResponse> getCarbonHistory(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @RequestParam Long userId,
            
            @Parameter(description = "Date de début (format: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDate startDate,
            
            @Parameter(description = "Date de fin (format: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDate endDate,
            
            @Parameter(description = "Numéro de page (commence à 0)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Taille de la page")
            @RequestParam(defaultValue = "10") int size) {
        
        CarbonHistoryResponse response = statsService.getCarbonHistory(
            userId, startDate, endDate, page, size
        );
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/stats/trees-planted
     * Récupère l'évolution des plantations d'arbres
     */
    @GetMapping("/trees-planted")
    @Operation(
        summary = "Évolution des plantations d'arbres",
        description = "Retourne l'évolution cumulative des arbres plantés et du carbone compensé"
    )
    public ResponseEntity<TreePlantationResponse> getTreesPlanted(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @RequestParam Long userId,
            
            @Parameter(description = "Nombre de mois à afficher")
            @RequestParam(defaultValue = "6") int months) {
        
        TreePlantationResponse response = statsService.getTreesPlantedEvolution(userId, months);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/stats/learning-progress
     * Récupère la progression de l'apprentissage
     */
    @GetMapping("/learning-progress")
    @Operation(
        summary = "Progression de l'apprentissage",
        description = "Retourne la progression détaillée de tous les parcours d'apprentissage"
    )
    public ResponseEntity<LearningProgressResponse> getLearningProgress(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @RequestParam Long userId) {
        
        LearningProgressResponse response = statsService.getLearningProgress(userId);
        return ResponseEntity.ok(response);
    }
}