package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.LearningPathRequest;
import com.project.dto.LearningPathResponse;
import com.project.dto.PersonalizeRequest;
import com.project.service.LearningService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LearningController.class)
class LearningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LearningService learningService;

    @Test
    void testGenerateLearningPath_Success() throws Exception {
        // Given
        LearningPathRequest request = new LearningPathRequest();
        request.setTopic("Java");
        request.setDifficulty("intermédiaire");
        request.setUserId(100L);

        LearningPathResponse response = new LearningPathResponse();
        response.setId(1L);
        response.setTitle("Parcours Java");
        response.setDifficulty("intermédiaire");

        when(learningService.generateLearningPath(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/learning/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Parcours Java"))
                .andExpect(jsonPath("$.difficulty").value("intermédiaire"));
    }

    @Test
    void testGenerateLearningPath_InvalidRequest() throws Exception {
        // Given - Request sans topic (invalide)
        LearningPathRequest request = new LearningPathRequest();
        request.setDifficulty("intermédiaire");

        // When & Then
        mockMvc.perform(post("/api/learning/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPersonalizeLearningPath_Success() throws Exception {
        // Given
        PersonalizeRequest request = new PersonalizeRequest();
        request.setPathId(1L);
        request.setUserLevel("avancé");

        LearningPathResponse response = new LearningPathResponse();
        response.setId(1L);
        response.setDifficulty("avancé");

        when(learningService.personalizeLearningPath(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/learning/personalize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.difficulty").value("avancé"));
    }

    @Test
    void testGetUserPaths_Success() throws Exception {
        // Given
        LearningPathResponse response1 = new LearningPathResponse();
        response1.setId(1L);
        response1.setTitle("Parcours 1");

        LearningPathResponse response2 = new LearningPathResponse();
        response2.setId(2L);
        response2.setTitle("Parcours 2");

        List<LearningPathResponse> responses = Arrays.asList(response1, response2);
        
        when(learningService.getUserPaths(100L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/learning/paths/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Parcours 1"))
                .andExpect(jsonPath("$[1].title").value("Parcours 2"));
    }
}