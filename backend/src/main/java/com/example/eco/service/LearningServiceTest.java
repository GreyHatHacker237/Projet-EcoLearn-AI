package com.project.service;

import com.project.config.OpenAIConfig;
import com.project.dto.LearningPathRequest;
import com.project.dto.LearningPathResponse;
import com.project.dto.PersonalizeRequest;
import com.project.model.LearningPath;
import com.project.repository.LearningPathRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningServiceTest {

    @Mock
    private LearningPathRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OpenAIConfig openAIConfig;

    @InjectMocks
    private LearningService learningService;

    private LearningPath mockLearningPath;

    @BeforeEach
    void setUp() {
        mockLearningPath = new LearningPath();
        mockLearningPath.setId(1L);
        mockLearningPath.setUserId(100L);
        mockLearningPath.setTitle("Parcours Java");
        mockLearningPath.setContent("Contenu du parcours");
        mockLearningPath.setDifficulty("intermédiaire");
    }

    @Test
    void testGenerateLearningPath_Success() {
        // Given
        LearningPathRequest request = new LearningPathRequest();
        request.setTopic("Java");
        request.setDifficulty("intermédiaire");
        request.setUserId(100L);

        when(repository.save(any(LearningPath.class))).thenReturn(mockLearningPath);

        // When
        LearningPathResponse response = learningService.generateLearningPath(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Parcours Java", response.getTitle());
        assertEquals("intermédiaire", response.getDifficulty());
        
        verify(repository, times(1)).save(any(LearningPath.class));
    }

    @Test
    void testPersonalizeLearningPath_Success() {
        // Given
        PersonalizeRequest request = new PersonalizeRequest();
        request.setPathId(1L);
        request.setUserLevel("avancé");

        when(repository.findById(1L)).thenReturn(Optional.of(mockLearningPath));
        when(repository.save(any(LearningPath.class))).thenReturn(mockLearningPath);

        // When
        LearningPathResponse response = learningService.personalizeLearningPath(request);

        // Then
        assertNotNull(response);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(LearningPath.class));
    }

    @Test
    void testPersonalizeLearningPath_PathNotFound() {
        // Given
        PersonalizeRequest request = new PersonalizeRequest();
        request.setPathId(999L);
        request.setUserLevel("avancé");

        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            learningService.personalizeLearningPath(request);
        });
        
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any(LearningPath.class));
    }

    @Test
    void testGetUserPaths_Success() {
        // Given
        Long userId = 100L;
        List<LearningPath> mockPaths = Arrays.asList(mockLearningPath);
        
        when(repository.findByUserId(userId)).thenReturn(mockPaths);

        // When
        List<LearningPathResponse> responses = learningService.getUserPaths(userId);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Parcours Java", responses.get(0).getTitle());
        
        verify(repository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetUserPaths_EmptyList() {
        // Given
        Long userId = 999L;
        when(repository.findByUserId(userId)).thenReturn(Arrays.asList());

        // When
        List<LearningPathResponse> responses = learningService.getUserPaths(userId);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        
        verify(repository, times(1)).findByUserId(userId);
    }
}