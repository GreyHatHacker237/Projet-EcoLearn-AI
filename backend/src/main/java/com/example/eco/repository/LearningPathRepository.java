package com.project.repository;

import com.project.model.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    
    List<LearningPath> findByUserId(Long userId);
    
    List<LearningPath> findByUserIdAndDifficulty(Long userId, String difficulty);
}