package com.example.eco.controller;

import com.example.eco.dto.LearningPathRequest;
import com.example.eco.dto.LearningPathResponse;
import com.example.eco.dto.PersonalizeRequest;
import com.example.eco.service.LearningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor // Lombok génère le constructeur pour injection
@Tag(name = "Learning", description = "API de gestion des parcours d'apprentissage")
public class LearningController {

    private final LearningService learningService;

    /**
     * POST /api/learning/generate
     * Génère un nouveau parcours d'apprentissage avec OpenAI
     */
    @PostMapping("/generate")
    @Operation(
        summary = "Générer un parcours d'apprentissage",
        description = "Crée un parcours personnalisé basé sur un sujet et un niveau de difficulté via OpenAI"
    )
    public ResponseEntity<LearningPathResponse> generateLearningPath(
            @Valid @RequestBody LearningPathRequest request) {
        try {
            LearningPathResponse response = learningService.generateLearningPath(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du parcours: " + e.getMessage(), e);
        }
    }

    /**
     * POST /api/learning/personalize
     * Personnalise un parcours existant selon le niveau de l'utilisateur
     */
    @PostMapping("/personalize")
    @Operation(
        summary = "Personnaliser un parcours",
        description = "Adapte un parcours existant au niveau spécifié de l'utilisateur"
    )
    public ResponseEntity<LearningPathResponse> personalizeLearningPath(
            @Valid @RequestBody PersonalizeRequest request) {
        try {
            LearningPathResponse response = learningService.personalizeLearningPath(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la personnalisation: " + e.getMessage(), e);
        }
    }

    /**
     * GET /api/learning/paths/{userId}
     * Récupère tous les parcours d'un utilisateur
     */
    @GetMapping("/paths/{userId}")
    @Operation(
        summary = "Récupérer les parcours d'un utilisateur",
        description = "Retourne la liste de tous les parcours d'apprentissage d'un utilisateur"
    )
    public ResponseEntity<List<LearningPathResponse>> getUserPaths(
            @PathVariable Long userId) {
        List<LearningPathResponse> paths = learningService.getUserPaths(userId);
        return ResponseEntity.ok(paths);
    }
}
