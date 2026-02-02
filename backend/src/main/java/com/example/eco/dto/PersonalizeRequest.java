package com.example.eco.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalizeRequest {
    private Long pathId;           // correspond à getPathId()
    private String userPreferences; // si ton service attend getUserLevel(), renommer
    private String learningStyle;   // correspond à getContent() ou ajuster selon le service

    // Optionnel : ajouter des getters personnalisés si le service attend d'autres noms
    public String getUserLevel() {
        return userPreferences; // mapping pour correspondre à l'ancien code
    }

    public String getContent() {
        return learningStyle;   // mapping pour correspondre à l'ancien code
    }
}
