package com.example.eco.controller;

import com.example.eco.dto.CarbonHistoryResponse;
import com.example.eco.dto.LearningProgressResponse;
import com.example.eco.dto.TreePlantationResponse;
import com.example.eco.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Statistics", description = "API de statistiques et analyses")
public class StatsController {

    @Autowired
    private StatsService statsService;

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

    @GetMapping("/carbon-history/paginated")
    @Operation(
        summary = "Historique carbone paginé",
        description = "Version paginée avec métadonnées complètes"
    )
    public ResponseEntity<CarbonHistoryResponse> getCarbonHistoryPaginated(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        CarbonHistoryResponse response = 
            statsService.getCarbonHistoryPaginated(userId, startDate, endDate, page, size);
        
        return ResponseEntity.ok(response);
    }
}