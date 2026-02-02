package com.example.eco.service;

import com.example.eco.model.LearningPath;
import com.example.eco.model.User;
import com.example.eco.repository.LearningPathRepository;
import com.example.eco.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProgressService {
    
    private final UserRepository userRepository;
    private final LearningPathRepository learningPathRepository;
    private final CarbonCalculationService carbonService;
    private final TreePlantationService treeService;
    
    /**
     * Récupère le tableau de bord complet d'un utilisateur
     */
    public UserDashboard getDashboard(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Récupérer toutes les données
        List<LearningPath> learningPaths = learningPathRepository.findByUserId(userId);
        CarbonCalculationService.CarbonStats carbonStats = carbonService.getUserCarbonStats(userId);
        TreePlantationService.PlantationStats plantationStats = treeService.getPlantationStats(userId);
        
        // Calculer les statistiques d'apprentissage
        LearningStats learningStats = calculateLearningStats(learningPaths);
        
        // Calculer les recommandations
        List<Recommendation> recommendations = generateRecommendations(
            learningStats, 
            carbonStats,
            plantationStats
        );
        
        // Calculer les badges
        List<Badge> badges = calculateBadges(user, learningStats, carbonStats, plantationStats);
        
        // Calculer le score écologique
        int ecoScore = calculateEcoScore(learningStats, carbonStats, plantationStats);
        
        return UserDashboard.builder()
            .user(user)
            .learningStats(learningStats)
            .carbonStats(carbonStats)
            .plantationStats(plantationStats)
            .recommendations(recommendations)
            .badges(badges)
            .ecoScore(ecoScore)
            .streakDays(calculateStreak(userId))
            .lastActive(user.getLastLogin() != null ? 
                ChronoUnit.DAYS.between(user.getLastLogin(), LocalDateTime.now()) : 0)
            .generatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * Calcule les statistiques d'apprentissage
     */
    private LearningStats calculateLearningStats(List<LearningPath> learningPaths) {
        if (learningPaths.isEmpty()) {
            return LearningStats.empty();
        }
        
        int totalPaths = learningPaths.size();
        int completedPaths = (int) learningPaths.stream()
            .filter(LearningPath::isCompleted)
            .count();
        
        int inProgressPaths = (int) learningPaths.stream()
            .filter(p -> !p.isCompleted() && p.getProgress() > 0)
            .count();
        
        DoubleSummaryStatistics progressStats = learningPaths.stream()
            .mapToDouble(LearningPath::getProgress)
            .summaryStatistics();
        
        // Temps total d'apprentissage estimé
        double totalHours = learningPaths.stream()
            .mapToDouble(LearningPath::getEstimatedHours)
            .sum();
        
        // Temps complété
        double completedHours = learningPaths.stream()
            .mapToDouble(p -> p.getEstimatedHours() * (p.getProgress() / 100.0))
            .sum();
        
        // Préférence de difficulté
        Map<String, Long> difficultyDistribution = learningPaths.stream()
            .collect(Collectors.groupingBy(
                LearningPath::getDifficulty,
                Collectors.counting()
            ));
        
        String preferredDifficulty = difficultyDistribution.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("beginner");
        
        // Préférence de thème
        Map<String, Long> topicDistribution = learningPaths.stream()
            .collect(Collectors.groupingBy(
                LearningPath::getTopic,
                Collectors.counting()
            ));
        
        String preferredTopic = topicDistribution.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("sustainability");
        
        return LearningStats.builder()
            .totalPaths(totalPaths)
            .completedPaths(completedPaths)
            .inProgressPaths(inProgressPaths)
            .averageProgress(progressStats.getAverage())
            .totalHours(totalHours)
            .completedHours(completedHours)
            .preferredDifficulty(preferredDifficulty)
            .preferredTopic(preferredTopic)
            .difficultyDistribution(difficultyDistribution)
            .topicDistribution(topicDistribution)
            .build();
    }
    
    /**
     * Génère des recommandations personnalisées
     */
    private List<Recommendation> generateRecommendations(
        LearningStats learningStats,
        CarbonCalculationService.CarbonStats carbonStats,
        TreePlantationService.PlantationStats plantationStats) {
        
        List<Recommendation> recommendations = new java.util.ArrayList<>();
        
        // Recommandations basées sur l'apprentissage
        if (learningStats.getCompletedPaths() == 0) {
            recommendations.add(Recommendation.builder()
                .type("learning")
                .title("Commencez votre premier parcours")
                .description("Découvrez notre parcours d'introduction au développement durable")
                .priority("high")
                .action("/learning/paths/beginner-sustainability")
                .build());
        }
        
        if (learningStats.getAverageProgress() < 50 && learningStats.getInProgressPaths() > 0) {
            recommendations.add(Recommendation.builder()
                .type("learning")
                .title("Continuez vos parcours en cours")
                .description(String.format("Vous avez %d parcours à moins de 50%%", 
                    learningStats.getInProgressPaths()))
                .priority("medium")
                .action("/learning/dashboard")
                .build());
        }
        
        // Recommandations basées sur le carbone
        if (carbonStats.getTotalSessions() > 0 && 
            carbonStats.getAverageSessionCarbon() > 0.1) {
            recommendations.add(Recommendation.builder()
                .type("carbon")
                .title("Optimisez votre empreinte carbone")
                .description(String.format("Votre moyenne est de %.3f kg CO2/session", 
                    carbonStats.getAverageSessionCarbon()))
                .priority("medium")
                .action("/carbon/tips")
                .build());
        }
        
        // Recommandations basées sur la plantation
        if (plantationStats.getTotalTrees() < carbonStats.getTotalTreesNeeded()) {
            int treesNeeded = carbonStats.getTotalTreesNeeded() - plantationStats.getTotalTrees();
            recommendations.add(Recommendation.builder()
                .type("plantation")
                .title("Compensez votre empreinte carbone")
                .description(String.format("Plantez %d arbres pour devenir neutre en carbone", treesNeeded))
                .priority("high")
                .action("/plantation/donate")
                .build());
        }
        
        return recommendations;
    }
    
    /**
     * Calcule les badges de l'utilisateur
     */
    private List<Badge> calculateBadges(
        User user,
        LearningStats learningStats,
        CarbonCalculationService.CarbonStats carbonStats,
        TreePlantationService.PlantationStats plantationStats) {
        
        List<Badge> badges = new java.util.ArrayList<>();
        
        // Badges d'apprentissage
        if (learningStats.getCompletedPaths() >= 1) {
            badges.add(createBadge("first_steps", "Premiers pas", 
                "A complété son premier parcours", "bronze"));
        }
        
        if (learningStats.getCompletedPaths() >= 5) {
            badges.add(createBadge("fast_learner", "Apprenant rapide", 
                "A complété 5 parcours", "silver"));
        }
        
        if (learningStats.getCompletedPaths() >= 10) {
            badges.add(createBadge("knowledge_master", "Maître du savoir", 
                "A complété 10 parcours", "gold"));
        }
        
        // Badges écologiques
        if (plantationStats.getTotalTrees() >= 10) {
            badges.add(createBadge("tree_planter", "Planteur d'arbres", 
                "A planté 10 arbres", "bronze"));
        }
        
        if (carbonStats.getTotalCarbonOffset() >= 100) {
            badges.add(createBadge("carbon_neutral", "Neutre en carbone", 
                "A compensé 100 kg de CO2", "silver"));
        }
        
        if (learningStats.getTotalHours() >= 50) {
            badges.add(createBadge("eco_warrior", "Guerrier écologique", 
                "50 heures d'apprentissage durable", "gold"));
        }
        
        // Badge de régularité
        int streak = calculateStreak(user.getId());
        if (streak >= 7) {
            badges.add(createBadge("weekly_streak", "Régulier", 
                "7 jours consécutifs d'activité", "bronze"));
        }
        
        if (streak >= 30) {
            badges.add(createBadge("monthly_streak", "Assidu", 
                "30 jours consécutifs d'activité", "silver"));
        }
        
        return badges;
    }
    
    /**
     * Calcule le score écologique (0-100)
     */
    private int calculateEcoScore(
        LearningStats learningStats,
        CarbonCalculationService.CarbonStats carbonStats,
        TreePlantationService.PlantationStats plantationStats) {
        
        int score = 0;
        
        // Points pour l'apprentissage (max 40)
        score += Math.min(learningStats.getCompletedPaths() * 4, 20);
        score += Math.min((int)(learningStats.getAverageProgress() * 0.2), 20);
        
        // Points pour le carbone (max 30)
        if (carbonStats.getTotalSessions() > 0) {
            double carbonEfficiency = 1.0 - Math.min(carbonStats.getAverageSessionCarbon() / 0.2, 1.0);
            score += (int)(carbonEfficiency * 30);
        }
        
        // Points pour la plantation (max 30)
        if (carbonStats.getTotalTreesNeeded() > 0) {
            double plantationRatio = (double) plantationStats.getTotalTrees() / 
                Math.max(carbonStats.getTotalTreesNeeded(), 1);
            score += Math.min((int)(plantationRatio * 30), 30);
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * Calcule la série de jours consécutifs
     */
    private int calculateStreak(Long userId) {
        // Implémentation simplifiée
        // En production, il faudrait vérifier les connexions quotidiennes
        return 3; // Valeur d'exemple
    }
    
    private Badge createBadge(String id, String name, String description, String level) {
        return Badge.builder()
            .id(id)
            .name(name)
            .description(description)
            .level(level)
            .earnedAt(LocalDateTime.now())
            .build();
    }
    
    // Classes internes
    @Data
    @Builder
    public static class UserDashboard {
        private User user;
        private LearningStats learningStats;
        private CarbonCalculationService.CarbonStats carbonStats;
        private TreePlantationService.PlantationStats plantationStats;
        private List<Recommendation