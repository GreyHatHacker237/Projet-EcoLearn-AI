package com.example.eco.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.eco.config.OpenAIConfig;
import com.example.eco.config.OpenAPIConfig;
import com.example.eco.dto.LearningPathRequest;
import com.example.eco.dto.LearningPathResponse;
import com.example.eco.dto.PersonalizeRequest;
import com.example.eco.model.LearningPath;
import com.example.eco.repository.LearningPathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LearningService {

    @Autowired
    private LearningPathRepository repository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private OpenAIConfig openAIConfig;
    
    @Value("${openai.api.url}")
    private String openAiUrl;

    public LearningPathResponse generateLearningPath(LearningPathRequest request) {
        
        String prompt = buildPrompt(request.getTopic(), request.getDifficulty());
        String generatedContent = callOpenAI(prompt);
        
        LearningPath learningPath = new LearningPath();
        learningPath.setUserId(request.getUserId());
        learningPath.setTitle("Parcours " + request.getTopic());
        learningPath.setContent(generatedContent);
        learningPath.setDifficulty(request.getDifficulty());
        learningPath.setCreatedAt(LocalDateTime.now());
        
        LearningPath saved = repository.save(learningPath);
        
        return mapToResponse(saved);
    }

    public LearningPathResponse personalizeLearningPath(PersonalizeRequest request) {
        
        LearningPath learningPath = repository.findById(request.getPathId())
            .orElseThrow(() -> new RuntimeException("Parcours introuvable"));
        
        String prompt = String.format(
            "Adapte ce contenu d'apprentissage selon les retours : %s\nStyle d'apprentissage : %s\n\nContenu actuel :\n%s\n\n" +
            "Adapte le contenu selon les retours et le style d'apprentissage.",
            request.getFeedback(),
            request.getLearningStyle(),
            learningPath.getContent()
        );
        
        String personalizedContent = callOpenAI(prompt);
        
        learningPath.setContent(personalizedContent);
        
        LearningPath updated = repository.save(learningPath);
        
        return mapToResponse(updated);
    }

    public List<LearningPathResponse> getUserPaths(Long userId) {
        return repository.findByUserId(userId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAIConfig.getApiKey());
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-3.5-turbo");
            body.put("messages", List.of(
                Map.of("role", "system", "content", "Tu es un expert en création de contenu éducatif."),
                Map.of("role", "user", "content", prompt)
            ));
            body.put("temperature", 0.7);
            body.put("max_tokens", 2000);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                openAiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );
            
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