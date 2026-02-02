package com.example.eco.service.impl;

import com.example.eco.config.OpenAIConfig;
import com.example.eco.dto.LearningPathRequest;
import com.example.eco.dto.LearningPathResponse;
import com.example.eco.model.LearningPath;
import com.example.eco.model.User;
import com.example.eco.repository.LearningPathRepository;
import com.example.eco.repository.UserRepository;
import com.example.eco.service.LearningService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningServiceImpl implements LearningService {
    
    private final OpenAiChatClient openAiChatClient;
    private final UserRepository userRepository;
    private final LearningPathRepository learningPathRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public LearningPathResponse generateLearningPath(Long userId, LearningPathRequest request) {
        try {
            log.info("Génération parcours IA pour utilisateur: {}", userId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            // 1. Construire le prompt
            String prompt = buildAIPrompt(user, request);
            
            // 2. Appeler OpenAI
            String aiResponse = openAiChatClient.call(prompt);
            log.debug("Réponse IA: {}", aiResponse);
            
            // 3. Parser et sauvegarder
            LearningPath path = parseAndSavePath(aiResponse, user, request);
            
            // 4. Retourner la réponse
            return buildResponse(path);
            
        } catch (Exception e) {
            log.error("Erreur génération parcours", e);
            throw new RuntimeException("Échec génération parcours: " + e.getMessage(), e);
        }
    }
    
    private String buildAIPrompt(User user, LearningPathRequest request) {
        return String.format("""
            Crée un parcours d'apprentissage sur le thème: %s
            Niveau: %s
            Durée: %s heures
            Objectifs: %s
            
            Format JSON requis:
            {
              "title": "Titre attractif",
              "description": "Description détaillée",
              "difficulty": "beginner/intermediate/advanced",
              "estimatedHours": 10,
              "modules": [
                {
                  "title": "Module 1",
                  "description": "Contenu module",
                  "duration": 2,
                  "resources": ["url1", "url2"],
                  "exercises": ["ex1", "ex2"]
                }
              ]
            }
            
            Conseils:
            1. Favoriser le développement durable
            2. Inclure des cas pratiques
            3. Adapter au niveau utilisateur
            """,
            request.getTopic(),
            request.getDifficulty(),
            request.getDuration(),
            request.getObjectives()
        );
    }
    
    private LearningPath parseAndSavePath(String aiResponse, User user, LearningPathRequest request) {
        try {
            JsonNode json = objectMapper.readTree(aiResponse);
            
            LearningPath path = new LearningPath();
            path.setUser(user);
            path.setTitle(json.get("title").asText());
            path.setDescription(json.get("description").asText());
            path.setDifficulty(json.get("difficulty").asText());
            path.setEstimatedHours(json.get("estimatedHours").asDouble());
            path.setModules(json.get("modules").toString());
            path.setTopic(request.getTopic());
            path.setGeneratedAt(LocalDateTime.now());
            path.setProgress(0.0);
            path.setCompleted(false);
            
            return learningPathRepository.save(path);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur parsing réponse IA", e);
        }
    }
    
    private LearningPathResponse buildResponse(LearningPath path) {
        return LearningPathResponse.builder()
            .pathId(path.getId())
            .title(path.getTitle())
            .description(path.getDescription())
            .difficulty(path.getDifficulty())
            .estimatedHours(path.getEstimatedHours())
            .modules(path.getModules())
            .progress(path.getProgress())
            .generatedAt(path.getGeneratedAt())
            .build();
    }
    
    @Override
    @Transactional
    public void updateProgress(Long pathId, double progressPercentage) {
        LearningPath path = learningPathRepository.findById(pathId)
            .orElseThrow(() -> new RuntimeException("Parcours non trouvé"));
        
        path.setProgress(progressPercentage);
        if (progressPercentage >= 100) {
            path.setCompleted(true);
            path.setCompletedAt(LocalDateTime.now());
        }
        
        learningPathRepository.save(path);
        log.info("Progression {} mise à jour à {}%", pathId, progressPercentage);
    }
    
    @Override
    public List<LearningPathResponse> getUserLearningPaths(Long userId) {
        return learningPathRepository.findByUserId(userId).stream()
            .map(this::buildResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<LearningPathResponse> recommendSimilarPaths(Long pathId) {
        // Logique de recommandation basique
        LearningPath current = learningPathRepository.findById(pathId)
            .orElseThrow(() -> new RuntimeException("Parcours non trouvé"));
        
        return learningPathRepository.findByTopic(current.getTopic()).stream()
            .filter(p -> !p.getId().equals(pathId))
            .limit(3)
            .map(this::buildResponse)
            .collect(Collectors.toList());
    }
}
