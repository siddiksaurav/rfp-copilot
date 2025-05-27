package com.rfp.copilot.service;
import com.rfp.copilot.prompt.PromptType;
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
        String technicalRequirements = documentService.extractByPromptType(sourceFilter, PromptType.TECHNICAL);

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
}