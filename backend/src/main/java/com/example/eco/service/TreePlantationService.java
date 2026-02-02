package com.example.eco.service;

import com.example.eco.model.TreePlantation;
import com.example.eco.model.User;
import com.example.eco.repository.TreePlantationRepository;
import com.example.eco.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TreePlantationService {
    
    private final TreePlantationRepository treePlantationRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    
    @Value("${ecolearn.tree.api.url:#{null}}")
    private String treeApiUrl;
    
    @Value("${ecolearn.tree.api.key:#{null}}")
    private String apiKey;
    
    @Value("${ecolearn.tree.cost-per-tree:0.10}")
    private double costPerTree;
    
    private static final double CARBON_OFFSET_PER_TREE = 21.77; // kg CO2/arbre/an
    
    /**
     * Planter des arbres pour compenser l'empreinte carbone
     */
    @Transactional
    public TreePlantation plantTrees(Long userId, int numberOfTrees, String projectName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Calculer le coût et l'offset carbone
        double totalCost = numberOfTrees * costPerTree;
        double carbonOffset = numberOfTrees * CARBON_OFFSET_PER_TREE;
        
        // Si l'API est configurée, planter via l'API
        String externalPlantationId = null;
        if (treeApiUrl != null && apiKey != null) {
            externalPlantationId = plantTreesViaAPI(numberOfTrees, projectName, user);
        }
        
        // Enregistrer en base
        TreePlantation plantation = TreePlantation.builder()
            .user(user)
            .treesPlanted(numberOfTrees)
            .projectName(projectName)
            .location(getPlantationLocation(projectName))
            .cost(totalCost)
            .carbonOffset(carbonOffset)
            .plantedAt(LocalDateTime.now())
            .externalPlantationId(externalPlantationId)
            .status(externalPlantationId != null ? "CONFIRMED" : "PENDING")
            .build();
        
        treePlantationRepository.save(plantation);
        log.info("{} arbres plantés pour l'utilisateur {}", numberOfTrees, userId);
        
        return plantation;
    }
    
    /**
     * Planter des arbres via une API externe (ex: Tree Nation, Ecologi)
     */
    private String plantTreesViaAPI(int numberOfTrees, String projectName, User user) {
        try {
            // Configuration des headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            // Construction du payload
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("trees", numberOfTrees);
            requestBody.put("project", projectName);
            requestBody.put("user_email", user.getEmail());
            requestBody.put("user_name", user.getName());
            requestBody.put("reference", "EcoLearn_" + System.currentTimeMillis());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Appel API
            ResponseEntity<Map> response = restTemplate.exchange(
                treeApiUrl + "/plant",
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK || 
                response.getStatusCode() == HttpStatus.CREATED) {
                
                Map<String, Object> responseBody = response.getBody();
                return (String) responseBody.get("plantation_id");
            }
            
        } catch (Exception e) {
            log.error("Erreur lors de l'appel à l'API de plantation", e);
            // Ne pas bloquer l'utilisateur si l'API échoue
        }
        
        return null;
    }
    
    /**
     * Récupérer l'historique des plantations d'un utilisateur
     */
    public List<TreePlantation> getUserPlantations(Long userId) {
        return treePlantationRepository.findByUserId(userId);
    }
    
    /**
     * Calculer les statistiques de plantation
     */
    public PlantationStats getPlantationStats(Long userId) {
        List<TreePlantation> plantations = getUserPlantations(userId);
        
        if (plantations.isEmpty()) {
            return PlantationStats.empty();
        }
        
        int totalTrees = plantations.stream()
            .mapToInt(TreePlantation::getTreesPlanted)
            .sum();
        
        double totalCarbonOffset = plantations.stream()
            .mapToDouble(TreePlantation::getCarbonOffset)
            .sum();
        
        double totalCost = plantations.stream()
            .mapToDouble(TreePlantation::getCost)
            .sum();
        
        // Regroupement par projet
        Map<String, Integer> treesByProject = plantations.stream()
            .collect(Collectors.groupingBy(
                TreePlantation::getProjectName,
                Collectors.summingInt(TreePlantation::getTreesPlanted)
            ));
        
        return PlantationStats.builder()
            .totalPlantations(plantations.size())
            .totalTrees(totalTrees)
            .totalCarbonOffset(totalCarbonOffset)
            .totalCost(totalCost)
            .treesByProject(treesByProject)
            .lastPlantation(plantations.get(plantations.size() - 1).getPlantedAt())
            .build();
    }
    
    /**
     * Synchroniser avec l'API externe (pour les plantations PENDING)
     */
    @Transactional
    public void syncPendingPlantations() {
        List<TreePlantation> pendingPlantations = treePlantationRepository.findByStatus("PENDING");
        
        for (TreePlantation plantation : pendingPlantations) {
            try {
                String externalId = plantTreesViaAPI(
                    plantation.getTreesPlanted(),
                    plantation.getProjectName(),
                    plantation.getUser()
                );
                
                if (externalId != null) {
                    plantation.setExternalPlantationId(externalId);
                    plantation.setStatus("CONFIRMED");
                    treePlantationRepository.save(plantation);
                    log.info("Plantation {} synchronisée", plantation.getId());
                }
            } catch (Exception e) {
                log.error("Échec de synchronisation pour la plantation {}", plantation.getId(), e);
            }
        }
    }
    
    /**
     * Obtenir la localisation selon le projet
     */
    private String getPlantationLocation(String projectName) {
        // Mapping des projets vers des localisations
        return switch (projectName.toLowerCase()) {
            case "amazon" -> "Brésil, Amazonie";
            case "africa" -> "Kenya, Afrique";
            case "indonesia" -> "Indonésie, Bornéo";
            case "europe" -> "France, Landes";
            default -> "Multiple locations";
        };
    }
    
    /**
     * Simuler l'impact environnemental
     */
    public EnvironmentalImpact calculateImpact(int treesPlanted) {
        // Estimations basées sur des données réelles
        double oxygenPerYear = treesPlanted * 118; // kg O2/arbre/an
        double co2Absorbed = treesPlanted * CARBON_OFFSET_PER_TREE;
        double waterFiltered = treesPlanted * 3785; // litres/an
        double habitatValue = treesPlanted * 50; // score arbitraire
        
        return EnvironmentalImpact.builder()
            .treesPlanted(treesPlanted)
            .oxygenProducedKg(oxygenPerYear)
            .co2AbsorbedKg(co2Absorbed)
            .waterFilteredLiters(waterFiltered)
            .habitatValue(habitatValue)
            .equivalentCarKm(co2Absorbed / 0.12) // 0.12 kg CO2/km voiture
            .equivalentFlights(co2Absorbed / 200) // 200 kg CO2/vol court
            .build();
    }
    
    // Classes internes
    @Data
    @Builder
    public static class PlantationStats {
        private int totalPlantations;
        private int totalTrees;
        private double totalCarbonOffset;
        private double totalCost;
        private Map<String, Integer> treesByProject;
        private LocalDateTime lastPlantation;
        
        public static PlantationStats empty() {
            return PlantationStats.builder()
                .totalPlantations(0)
                .totalTrees(0)
                .totalCarbonOffset(0)
                .totalCost(0)
                .treesByProject(Map.of())
                .lastPlantation(null)
                .build();
        }
    }
    
    @Data
    @Builder
    public static class EnvironmentalImpact {
        private int treesPlanted;
        private double oxygenProducedKg;
        private double co2AbsorbedKg;
        private double waterFilteredLiters;
        private double habitatValue;
        private double equivalentCarKm;
        private double equivalentFlights;
    }
}