package com.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.config.OpenAIConfig;
import com.project.dto.LearningPathRequest;
import com.project.dto.LearningPathResponse;
import com.project.dto.PersonalizeRequest;
import com.project.model.LearningPath;
import com.project.repository.LearningPathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningService {

    private final LearningPathRepository repository;
    private final RestTemplate restTemplate;
    private final OpenAIConfig openAIConfig;
    
    @Value("${openai.api.url}")
    private String openAiUrl;

    /**
     * Génère un parcours d'apprentissage avec OpenAI
     */
    public LearningPathResponse generateLearningPath(LearningPathRequest request) {
        
        // Construction du prompt pour OpenAI
        String prompt = buildPrompt(request.getTopic(), request.getDifficulty());
        
        // Appel à l'API OpenAI
        String generatedContent = callOpenAI(prompt);
        
        // Sauvegarde en base de données
        LearningPath learningPath = new LearningPath();
        learningPath.setUserId(request.getUserId());
        learningPath.setTitle("Parcours " + request.getTopic());
        learningPath.setContent(generatedContent);
        learningPath.setDifficulty(request.getDifficulty());
        
        LearningPath saved = repository.save(learningPath);
        
        return mapToResponse(saved);
    }

    /**
     * Personnalise un parcours existant selon le niveau
     */
    public LearningPathResponse personalizeLearningPath(PersonalizeRequest request) {
        
        LearningPath learningPath = repository.findById(request.getPathId())
            .orElseThrow(() -> new RuntimeException("Parcours introuvable"));
        
        // Prompt de personnalisation
        String prompt = String.format(
            "Adapte ce contenu d'apprentissage au niveau : %s\n\nContenu actuel :\n%s\n\n" +
            "Simplifie ou approfondit selon le niveau demandé.",
            request.getUserLevel(),
            learningPath.getContent()
        );
        
        // Appel OpenAI
        String personalizedContent = callOpenAI(prompt);
        
        // Mise à jour
        learningPath.setContent(personalizedContent);
        learningPath.setDifficulty(request.getUserLevel());
        
        LearningPath updated = repository.save(learningPath);
        
        return mapToResponse(updated);
    }

    /**
     * Récupère tous les parcours d'un utilisateur
     */
    public List<LearningPathResponse> getUserPaths(Long userId) {
        return repository.findByUserId(userId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private String buildPrompt(String topic, String difficulty) {
        return String.format("""
            Crée un parcours d'apprentissage structuré sur le sujet : %s
            Niveau de difficulté : %s
            
            Le parcours doit contenir :
            - Une introduction au sujet
            - 5 modules d'apprentissage progressifs
            - Des exercices pratiques pour chaque module
            - Des ressources complémentaires
            
            Format JSON attendu :
            {
                "title": "Titre du parcours",
                "modules": [
                    {
                        "name": "Module 1",
                        "content": "Contenu détaillé",
                        "exercises": ["Exercice 1", "Exercice 2"]
                    }
                ]
            }
            """, topic, difficulty);
    }

    private String callOpenAI(String prompt) {
        try {
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAIConfig.getApiKey());
            
            // Body
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-3.5-turbo");
            body.put("messages", List.of(
                Map.of("role", "system", "content", "Tu es un expert en création de contenu éducatif."),
                Map.of("role", "user", "content", prompt)
            ));
            body.put("temperature", 0.7);
            body.put("max_tokens", 2000);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            // Appel API
            ResponseEntity<String> response = restTemplate.exchange(
                openAiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            // Extraction du contenu
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("choices").get(0)
                .path("message").path("content").asText();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel à OpenAI: " + e.getMessage());
        }
    }

    private LearningPathResponse mapToResponse(LearningPath entity) {
        LearningPathResponse response = new LearningPathResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setDifficulty(entity.getDifficulty());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}