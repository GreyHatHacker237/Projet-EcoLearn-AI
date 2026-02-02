package com.example.eco.service.impl;

import com.example.eco.model.TreePlantation;
import com.example.eco.model.User;
import com.example.eco.repository.TreePlantationRepository;
import com.example.eco.repository.UserRepository;
import com.example.eco.service.TreePlantationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TreePlantationServiceImpl implements TreePlantationService {
    
    private final TreePlantationRepository treePlantationRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    
    @Value("\${ecolearn.tree.api.enabled:false}")
    private boolean apiEnabled;
    
    @Value("\${ecolearn.tree.cost.per.tree:0.10}")
    private double costPerTree;
    
    private static final double CARBON_OFFSET_PER_TREE = 21.77; // kg CO2/arbre/an
    
    @Override
    @Transactional
    public TreePlantation plantTrees(Long userId, int numberOfTrees, String projectName) {
        log.info("Plantation de {} arbres pour l'utilisateur {}", numberOfTrees, userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Calculs
        double totalCost = numberOfTrees * costPerTree;
        double carbonOffset = numberOfTrees * CARBON_OFFSET_PER_TREE;
        
        // Création de l'entité
        TreePlantation plantation = new TreePlantation();
        plantation.setUser(user);
        plantation.setTreesPlanted(numberOfTrees);
        plantation.setProjectName(projectName);
        plantation.setLocation(getProjectLocation(projectName));
        plantation.setCost(totalCost);
        plantation.setCarbonOffset(carbonOffset);
        plantation.setPlantedAt(LocalDateTime.now());
        plantation.setStatus("PENDING");
        
        // Synchronisation API si activée
        if (apiEnabled) {
            String externalId = syncWithExternalAPI(plantation, user);
            plantation.setExternalPlantationId(externalId);
            plantation.setStatus(externalId != null ? "CONFIRMED" : "PENDING");
        }
        
        return treePlantationRepository.save(plantation);
    }
    
    @Override
    public void syncWithExternalAPI() {
        if (!apiEnabled) {
            log.info("API externe désactivée");
            return;
        }
        
        List<TreePlantation> pending = treePlantationRepository.findByStatus("PENDING");
        log.info("Synchronisation de {} plantations", pending.size());
        
        for (TreePlantation plantation : pending) {
            try {
                String externalId = callPlantationAPI(plantation);
                if (externalId != null) {
                    plantation.setExternalPlantationId(externalId);
                    plantation.setStatus("CONFIRMED");
                    treePlantationRepository.save(plantation);
                    log.info("Plantation {} synchronisée", plantation.getId());
                }
            } catch (Exception e) {
                log.error("Échec synchronisation plantation {}", plantation.getId(), e);
            }
        }
    }
    
    @Override
    public List<TreePlantation> getUserPlantations(Long userId) {
        return treePlantationRepository.findByUserId(userId);
    }
    
    @Override
    public Map<String, Object> getPlantationStats(Long userId) {
        List<TreePlantation> plantations = getUserPlantations(userId);
        
        if (plantations.isEmpty()) {
            return createEmptyStats();
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
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlantations", plantations.size());
        stats.put("totalTrees", totalTrees);
        stats.put("totalCarbonOffset", Math.round(totalCarbonOffset * 100.0) / 100.0);
        stats.put("totalCost", Math.round(totalCost * 100.0) / 100.0);
        stats.put("treesByProject", treesByProject);
        stats.put("lastPlantation", plantations.get(plantations.size() - 1).getPlantedAt());
        
        return stats;
    }
    
    @Override
    public Map<String, Double> calculateEnvironmentalImpact(int treesPlanted) {
        Map<String, Double> impact = new HashMap<>();
        
        // Estimations basées sur des données scientifiques
        impact.put("co2AbsorbedKg", treesPlanted * CARBON_OFFSET_PER_TREE);
        impact.put("oxygenProducedKg", treesPlanted * 118.0);
        impact.put("waterFilteredLiters", treesPlanted * 3785.0);
        
        // Équivalents
        impact.put("equivalentCarKm", impact.get("co2AbsorbedKg") / 0.12); // kg CO2/km
        impact.put("equivalentHomes", impact.get("co2AbsorbedKg") / 6000.0); // kg CO2/maison/an
        
        return impact;
    }
    
    // Méthodes privées
    private String syncWithExternalAPI(TreePlantation plantation, User user) {
        // Implémentation factice - à remplacer par l'API réelle
        log.info("Appel API externe pour {} arbres", plantation.getTreesPlanted());
        
        // Simuler un délai API
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Retourner un ID factice
        return apiEnabled ? "EXT_" + System.currentTimeMillis() : null;
    }
    
    private String callPlantationAPI(TreePlantation plantation) {
        // TODO: Implémenter l'appel réel à Tree Nation/Ecologi
        // Exemple:
        // String url = "https://api.treenation.org/v1/plant";
        // HttpHeaders headers = new HttpHeaders();
        // headers.setBearerAuth(apiKey);
        // ...
        
        return "MOCK_API_ID_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private String getProjectLocation(String projectName) {
        return switch (projectName.toLowerCase()) {
            case "amazon" -> "Amazon Rainforest, Brazil";
            case "africa" -> "Kenya, Africa";
            case "indonesia" -> "Borneo, Indonesia";
            case "europe" -> "France, Europe";
            default -> "Global Reforestation Project";
        };
    }
    
    private Map<String, Object> createEmptyStats() {
        Map<String, Object> empty = new HashMap<>();
        empty.put("totalPlantations", 0);
        empty.put("totalTrees", 0);
        empty.put("totalCarbonOffset", 0.0);
        empty.put("totalCost", 0.0);
        empty.put("treesByProject", new HashMap<>());
        empty.put("lastPlantation", null);
        return empty;
    }
}