package com.rfp.copilot.service;

import com.rfp.copilot.dto.BidDecision;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RequirementGatherService {

    private final DocumentProcessingService documentService;
    private final LlmService llmService;

    public RequirementGatherService(DocumentProcessingService documentService, LlmService llmService) {
        this.documentService = documentService;
        this.llmService = llmService;
    }

    public String analyzeTorSuitability(String sourceFilter) {
        String technicalRequirements = documentService.extractTechnicalRequirements(sourceFilter);

        // Prepare data for LLM analysis
        Map<String, Object> analysisData = new HashMap<>();
        analysisData.put("requirements", technicalRequirements);

        String prompt = generateAnalysisPrompt(analysisData);

        return llmService.generateResponse(prompt);
    }

    public String generateAnalysisPrompt(Map<String, Object> analysisData) {
        return """
                       TOR Technical Requirements:
                       %s
                
                       From the full RFP content provided below, extract and organize all important requirements, items, and conditions.
                
                       ✅ Identify all required formats, documents, deliverables, and evaluation criteria — from any section of the RFP. \s
                       📌 If any part refers to another section (e.g., “see Section 5”, “refer to PDS”), include those referenced details **only if they appear in the provided context**. \s
                       🧠 Group the extracted points clearly by category to assist in writing a compliant and complete proposal.
                
                       📋 After the structured output, provide an additional section listing **any potentially missing or unclear points** that might require clarification or follow-up with the issuing authority.
                
                       Return the output in the following format:
                
                       ---
                
                       ### Required Documents
                       - ...
                
                       ### Technical Requirements
                       - ...
                
                       ### Timeline & Milestones
                       - ...
                
                       ### Evaluation Criteria
                       - ...
                
                       ### Compliance / Legal / Other Conditions
                       - ...
                
                       ---
                
                       ### ⚠️ Possible Missing or Unclear Points
                       - [e.g., “No budget breakdown instructions provided.”]
                       - [e.g., “Timeline for deliverables not clearly defined.”]
                
                       ❗Only use information present in the RFP context. Do not hallucinate or invent missing parts, but you may flag potential gaps for review.
                
                """.formatted(
                analysisData.get("requirements")
        );
    }


//    private String generateAnalysisPrompt(Map<String, Object> analysisData) {
//        @SuppressWarnings("unchecked")
//        List<String> requirements = (List<String>) analysisData.get("technicalRequirements");
//        @SuppressWarnings("unchecked")
//        Map<String, Object> timeline = (Map<String, Object>) analysisData.get("timelineInfo");
//        @SuppressWarnings("unchecked")
//        Map<String, Object> capabilities = (Map<String, Object>) analysisData.get("companyCapabilities");
//
//        StringBuilder promptBuilder = new StringBuilder();
//        promptBuilder.append("You are an AI consultant helping a company decide whether to bid on a project.\n\n");
//        promptBuilder.append("Please analyze the following information and determine if the company should bid on this project based on two main criteria:\n");
//        promptBuilder.append("1. Whether the technical requirements are realistic given the project timeline\n");
//        promptBuilder.append("2. Whether the company has the necessary expertise for the technical requirements\n\n");
//
//        // Add technical requirements
//        promptBuilder.append("TECHNICAL REQUIREMENTS:\n");
//        for (String req : requirements) {
//            promptBuilder.append("- ").append(req).append("\n");
//        }
//        promptBuilder.append("\n");
//
//        // Add timeline information
//        promptBuilder.append("PROJECT TIMELINE:\n");
//        promptBuilder.append("- Start date: ").append(timeline.get("startDate")).append("\n");
//        promptBuilder.append("- End date: ").append(timeline.get("endDate")).append("\n");
//        promptBuilder.append("- Duration (days): ").append(timeline.get("durationDays")).append("\n");
//        promptBuilder.append("- Key milestones: ").append(timeline.get("milestones")).append("\n\n");
//
//        // Add company capabilities
//        promptBuilder.append("COMPANY CAPABILITIES:\n");
//        promptBuilder.append("- Technical expertise: ").append(capabilities.get("technicalExpertise")).append("\n");
//        promptBuilder.append("- Team size: ").append(capabilities.get("teamSize")).append("\n");
//        promptBuilder.append("- Past project experience: ").append(capabilities.get("pastProjects")).append("\n");
//        promptBuilder.append("- Available resources: ").append(capabilities.get("availableResources")).append("\n\n");
//
//        // Add specific analysis instructions
//        promptBuilder.append("ANALYSIS INSTRUCTIONS:\n");
//        promptBuilder.append("1. First, identify if any technical requirements involve specialized expertise in AI, LLM model building, or coding automation tools.\n");
//        promptBuilder.append("2. Check if the company has matching expertise for these specialized requirements.\n");
//        promptBuilder.append("3. Analyze if the timeline is realistic for the technical requirements.\n");
//        promptBuilder.append("4. Provide a final recommendation (BID or NO_BID) with detailed reasoning.\n");
//        promptBuilder.append("5. Format your response as JSON with the following structure:\n");
//        promptBuilder.append("{\n");
//        promptBuilder.append("  \"decision\": \"BID\" or \"NO_BID\",\n");
//        promptBuilder.append("  \"confidence\": 0-100,\n");
//        promptBuilder.append("  \"unrealisticTimeline\": true/false,\n");
//        promptBuilder.append("  \"lackOfExpertise\": true/false,\n");
//        promptBuilder.append("  \"reasoning\": \"detailed explanation\",\n");
//        promptBuilder.append("  \"riskAreas\": [\"list\", \"of\", \"risks\"]\n");
//        promptBuilder.append("}\n");
//
//        return promptBuilder.toString();
//    }

    /**
     * Parses the LLM response into a structured BidDecision object
     */
    private BidDecision parseLlmResponse(String llmResponse) {
        try {
            // Here you would implement JSON parsing of the LLM response
            // For simplicity, this is a placeholder
            // In a real implementation, use a JSON library like Jackson

            BidDecision decision = new BidDecision();
            // Parse JSON from llmResponse and populate decision object

            return decision;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LLM response", e);
        }
    }


}