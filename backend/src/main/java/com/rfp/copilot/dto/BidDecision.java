package com.rfp.copilot.dto;

import java.util.List;

public class BidDecision {
    private String decision;
    private int confidence;
    private boolean unrealisticTimeline;
    private boolean lackOfExpertise;
    private String reasoning;
    private List<String> riskAreas;

    // Getters and setters
    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public boolean isUnrealisticTimeline() {
        return unrealisticTimeline;
    }

    public void setUnrealisticTimeline(boolean unrealisticTimeline) {
        this.unrealisticTimeline = unrealisticTimeline;
    }

    public boolean isLackOfExpertise() {
        return lackOfExpertise;
    }

    public void setLackOfExpertise(boolean lackOfExpertise) {
        this.lackOfExpertise = lackOfExpertise;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public List<String> getRiskAreas() {
        return riskAreas;
    }

    public void setRiskAreas(List<String> riskAreas) {
        this.riskAreas = riskAreas;
    }
}
