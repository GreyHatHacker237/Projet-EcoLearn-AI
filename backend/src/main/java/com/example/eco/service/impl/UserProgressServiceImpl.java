package com.example.eco.service.impl;

import com.example.eco.model.LearningPath;
import com.example.eco.model.User;
import com.example.eco.repository.LearningPathRepository;
import com.example.eco.repository.UserRepository;
import com.example.eco.service.CarbonCalculationService;
import com.example.eco.service.TreePlantationService;
import com.example.eco.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProgressServiceImpl implements UserProgressService {
    
    private final UserRepository userRepository;
    private final LearningPathRepository learningPathRepository;
    private final CarbonCalculationService carbonService;
    private final TreePlantationService treeService;
    
    @Override
    public UserDashboard getDashboard(Long userId) {
        log.info("Génération dashboard pour l'utilisateur {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        UserDashboard dashboard = new UserDashboard();
        dashboard.setUser(user);
        dashboard.setLearningStats(calculateLearningStats(userId));
        dashboard.setCarbonStats(calculateCarbonStats(userId));
        dashboard.setPlantationStats(calculatePlantationStats(userId));
        dashboard.setRecommendations(generateRecommendations(userId));
        dashboard.setBadges(calculateBadges(userId));
        dashboard.setEcoScore(calculateEcoScore(userId));
        
        return dashboard;
    }
    
    @Override
    public void updateUserStats(Long userId, String activityType, double value) {
        log.info("Mise à jour stats: utilisateur {}, activité {}, valeur {}", 
                 userId, activityType, value);
        
        // Ici, tu pourrais enregistrer l'activité dans une table de log
        // Pour l'instant, on se contente de logger
        switch (activityType) {
            case "learning_session":
                log.info("Session d'apprentissage: {} heures", value);
                break;
            case "carbon_calculation":
                log.info("Calcul carbone: {} kg CO2", value);
                break;
            case "tree_plantation":
                log.info("Plantation: {} arbres", value);
                break;
            default:
                log.info("Activité inconnue: {}", activityType);
        }
    }
    
    @Override
    public List<Recommendation> generateRecommendations(Long userId) {
        List<Recommendation> recommendations = new ArrayList<>();
        
        // Récupérer les stats
        Map<String, Object> learningStats = calculateLearningStats(userId);
        Map<String, Object> carbonStats = calculateCarbonStats(userId);
        Map<String, Object> plantationStats = calculatePlantationStats(userId);
        
        int completedPaths = (int) learningStats.get("completedPaths");
        double totalCarbon = (double) carbonStats.get("total");
        int treesPlanted = (int) plantationStats.get("totalTrees");
        int treesNeeded = (int) carbonStats.getOrDefault("treesNeeded", 0);
        double avgProgress = (double) learningStats.getOrDefault("averageProgress", 0.0);
        
        // 1. Recommandation pour débutants
        if (completedPaths == 0) {
            recommendations.add(createRecommendation(
                "learning",
                "🎯 Commencez votre voyage d'apprentissage",
                "Découvrez notre parcours d'introduction au développement durable",
                "high",
                "/learning/paths/beginner-sustainability"
            ));
        }
        
        // 2. Recommandation pour compensation carbone
        if (totalCarbon > 0 && treesPlanted < treesNeeded) {
            int treesToPlant = treesNeeded - treesPlanted;
            recommendations.add(createRecommendation(
                "carbon",
                "🌳 Compensez votre empreinte carbone",
                String.format("Plantez %d arbres pour devenir neutre en carbone", treesToPlant),
                "high",
                "/carbon/offset"
            ));
        }
        
        // 3. Recommandation de progression
        if (avgProgress > 0 && avgProgress < 50) {
            recommendations.add(createRecommendation(
                "progress",
                "📚 Continuez vos parcours en cours",
                String.format("Vous êtes à %.1f%% de progression. Continuez comme ça !", avgProgress),
                "medium",
                "/learning/dashboard"
            ));
        }
        
        // 4. Recommandation pour diversité d'apprentissage
        if (completedPaths >= 3) {
            Map<String, Long> topicDistribution = (Map<String, Long>) learningStats.get("topicDistribution");
            if (topicDistribution != null && topicDistribution.size() < 2) {
                recommendations.add(createRecommendation(
                    "diversity",
                    "🌍 Explorez de nouveaux sujets",
                    "Diversifiez vos connaissances en explorant d'autres thèmes durables",
                    "low",
                    "/learning/topics"
                ));
            }
        }
        
        // 5. Recommandation pour engagement régulier
        int streak = calculateStreak(userId);
        if (streak < 3) {
            recommendations.add(createRecommendation(
                "engagement",
                "🔥 Maintenez votre engagement",
                "Revenez demain pour continuer votre apprentissage durable",
                "medium",
                "/daily-challenge"
            ));
        }
        
        return recommendations;
    }
    
    @Override
    public List<Badge> calculateBadges(Long userId) {
        List<Badge> badges = new ArrayList<>();
        
        Map<String, Object> learningStats = calculateLearningStats(userId);
        Map<String, Object> carbonStats = calculateCarbonStats(userId);
        Map<String, Object> plantationStats = calculatePlantationStats(userId);
        
        int completedPaths = (int) learningStats.get("completedPaths");
        int totalTrees = (int) plantationStats.get("totalTrees");
        double totalCarbonOffset = (double) plantationStats.get("totalCarbonOffset");
        int streak = calculateStreak(userId);
        
        // Badges d'apprentissage
        if (completedPaths >= 1) {
            badges.add(createBadge(
                "first_steps",
                "Premiers Pas",
                "A complété son premier parcours d'apprentissage",
                "🥉",
                LocalDateTime.now().minusDays(completedPaths).toString()
            ));
        }
        
        if (completedPaths >= 5) {
            badges.add(createBadge(
                "fast_learner", 
                "Apprenant Rapide",
                "A complété 5 parcours d'apprentissage",
                "🥈",
                LocalDateTime.now().minusDays(completedPaths / 2).toString()
            ));
        }
        
        if (completedPaths >= 10) {
            badges.add(createBadge(
                "knowledge_master",
                "Maître du Savoir",
                "A complété 10 parcours d'apprentissage",
                "🥇",
                LocalDateTime.now().toString()
            ));
        }
        
        // Badges écologiques
        if (totalTrees >= 10) {
            badges.add(createBadge(
                "tree_planter",
                "Planteur d'Arbres",
                "A planté 10 arbres pour la planète",
                "🌱",
                LocalDateTime.now().toString()
            ));
        }
        
        if (totalCarbonOffset >= 100) {
            badges.add(createBadge(
                "carbon_neutral",
                "Neutre en Carbone",
                "A compensé 100 kg de CO2",
                "♻️",
                LocalDateTime.now().toString()
            ));
        }
        
        if (totalCarbonOffset >= 500) {
            badges.add(createBadge(
                "climate_hero",
                "Héros du Climat",
                "A compensé 500 kg de CO2",
                "🌍",
                LocalDateTime.now().toString()
            ));
        }
        
        // Badges de régularité
        if (streak >= 7) {
            badges.add(createBadge(
                "weekly_streak",
                "Régulier Hebdomadaire",
                "7 jours consécutifs d'apprentissage",
                "🔥",
                LocalDateTime.now().toString()
            ));
        }
        
        if (streak >= 30) {
            badges.add(createBadge(
                "monthly_streak",
                "Assidu Mensuel",
                "30 jours consécutifs d'apprentissage",
                "⭐",
                LocalDateTime.now().toString()
            ));
        }
        
        // Badge spécial EcoWarrior
        if (completedPaths >= 5 && totalTrees >= 10 && totalCarbonOffset >= 100) {
            badges.add(createBadge(
                "eco_warrior",
                "Guerrier Écologique",
                "Expert en apprentissage durable et action climatique",
                "🏆",
                LocalDateTime.now().toString()
            ));
        }
        
        return badges;
    }
    
    // Méthodes de calcul privées
    private Map<String, Object> calculateLearningStats(Long userId) {
        List<LearningPath> paths = learningPathRepository.findByUserId(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPaths", paths.size());
        stats.put("completedPaths", (int) paths.stream().filter(LearningPath::isCompleted).count());
        stats.put("inProgressPaths", (int) paths.stream()
            .filter(p -> p.getProgress() > 0 && !p.isCompleted()).count());
        
        if (!paths.isEmpty()) {
            // Progression moyenne
            double avgProgress = paths.stream()
                .mapToDouble(LearningPath::getProgress)
                .average()
                .orElse(0.0);
            stats.put("averageProgress", Math.round(avgProgress * 10.0) / 10.0);
            
            // Heures totales
            double totalHours = paths.stream()
                .mapToDouble(LearningPath::getEstimatedHours)
                .sum();
            stats.put("totalHours", Math.round(totalHours * 10.0) / 10.0);
            
            // Heures complétées
            double completedHours = paths.stream()
                .mapToDouble(p -> p.getEstimatedHours() * (p.getProgress() / 100.0))
                .sum();
            stats.put("completedHours", Math.round(completedHours * 10.0) / 10.0);
            
            // Distribution par difficulté
            Map<String, Long> difficultyCount = paths.stream()
                .collect(Collectors.groupingBy(
                    LearningPath::getDifficulty,
                    Collectors.counting()
                ));
            stats.put("difficultyDistribution", difficultyCount);
            
            // Difficulté préférée
            String preferredDifficulty = difficultyCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("beginner");
            stats.put("preferredDifficulty", preferredDifficulty);
            
            // Distribution par thème
            Map<String, Long> topicCount = paths.stream()
                .collect(Collectors.groupingBy(
                    LearningPath::getTopic,
                    Collectors.counting()
                ));
            stats.put("topicDistribution", topicCount);
            
            // Thème préféré
            String preferredTopic = topicCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("sustainability");
            stats.put("preferredTopic", preferredTopic);
            
        } else {
            stats.put("averageProgress", 0.0);
            stats.put("totalHours", 0.0);
            stats.put("completedHours", 0.0);
            stats.put("difficultyDistribution", new HashMap<>());
            stats.put("preferredDifficulty", "beginner");
            stats.put("topicDistribution", new HashMap<>());
            stats.put("preferredTopic", "sustainability");
        }
        
        return stats;
    }
    
    private Map<String, Object> calculateCarbonStats(Long userId) {
        try {
            // Utiliser le CarbonCalculationService
            CarbonCalculationService.SessionData sessionData = new CarbonCalculationService.SessionData();
            Map<String, Double> carbonData = carbonService.getCarbonStatistics(userId, "month");
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", carbonData.getOrDefault("total", 0.0));
            stats.put("average", carbonData.getOrDefault("average", 0.0));
            stats.put("sessions", carbonData.getOrDefault("sessions", 0.0));
            stats.put("treesNeeded", carbonData.getOrDefault("treesNeeded", 0.0));
            
            // Calculer l'efficacité carbone (score de 0 à 100)
            double carbonEfficiency = 0.0;
            if (carbonData.getOrDefault("average", 0.0) > 0) {
                // Moins de carbone = meilleur score
                carbonEfficiency = Math.max(0, 100 - (carbonData.get("average") * 1000));
            }
            stats.put("carbonEfficiency", Math.round(carbonEfficiency * 10.0) / 10.0);
            
            return stats;
            
        } catch (Exception e) {
            log.warn("Erreur calcul stats carbone, retour valeurs par défaut", e);
            return createDefaultCarbonStats();
        }
    }
    
    private Map<String, Object> calculatePlantationStats(Long userId) {
        try {
            // Utiliser le TreePlantationService
            return treeService.getPlantationStats(userId);
        } catch (Exception e) {
            log.warn("Erreur calcul stats plantation, retour valeurs par défaut", e);
            return createDefaultPlantationStats();
        }
    }
    
    private int calculateEcoScore(Long userId) {
        Map<String, Object> learningStats = calculateLearningStats(userId);
        Map<String, Object> carbonStats = calculateCarbonStats(userId);
        Map<String, Object> plantationStats = calculatePlantationStats(userId);
        
        int score = 0;
        
        // Points pour l'apprentissage (max 40)
        int completedPaths = (int) learningStats.get("completedPaths");
        score += Math.min(completedPaths * 4, 20);
        
        double avgProgress = (double) learningStats.get("averageProgress");
        score += Math.min((int)(avgProgress * 0.4), 20);
        
        // Points pour le carbone (max 30)
        double carbonEfficiency = (double) carbonStats.getOrDefault("carbonEfficiency", 0.0);
        score += Math.min((int)(carbonEfficiency * 0.3), 30);
        
        // Points pour la plantation (max 30)
        int totalTrees = (int) plantationStats.getOrDefault("totalTrees", 0);
        score += Math.min(totalTrees * 3, 30);
        
        // Bonus pour engagement (max 10)
        int streak = calculateStreak(userId);
        score += Math.min(streak, 10);
        
        return Math.min(score, 100);
    }
    
    private int calculateStreak(Long userId) {
        // Implémentation simplifiée
        // En production, il faudrait vérifier les dates de connexion/activité
        return new Random().nextInt(15) + 1; // Valeur aléatoire pour la démo
    }
    
    // Méthodes utilitaires
    private Recommendation createRecommendation(String type, String title, 
                                              String description, String priority, String actionUrl) {
        Recommendation rec = new Recommendation();
        rec.setType(type);
        rec.setTitle(title);
        rec.setDescription(description);
        rec.setPriority(priority);
        rec.setActionUrl(actionUrl);
        return rec;
    }
    
    private Badge createBadge(String id, String name, String description, 
                             String iconUrl, String earnedDate) {
        Badge badge = new Badge();
        badge.setId(id);
        badge.setName(name);
        badge.setDescription(description);
        badge.setIconUrl(iconUrl);
        badge.setEarnedDate(earnedDate);
        return badge;
    }
    
    private Map<String, Object> createDefaultCarbonStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 0.0);
        stats.put("average", 0.0);
        stats.put("sessions", 0.0);
        stats.put("treesNeeded", 0);
        stats.put("carbonEfficiency", 100.0);
        return stats;
    }
    
    private Map<String, Object> createDefaultPlantationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlantations", 0);
        stats.put("totalTrees", 0);
        stats.put("totalCarbonOffset", 0.0);
        stats.put("totalCost", 0.0);
        stats.put("treesByProject", new HashMap<>());
        stats.put("lastPlantation", null);
        return stats;
    }
}