package com.example.eco.dto;

public class PersonalizeRequest {
    private Long pathId;
    private String feedback;
    private String learningStyle;

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }
    
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    
    public String getLearningStyle() { return learningStyle; }
    public void setLearningStyle(String learningStyle) { this.learningStyle = learningStyle; }
    public void setUserLevel(String string) {
        throw new UnsupportedOperationException("Unimplemented method 'setUserLevel'");
    }
}