package com.rfp.copilot.service;

import com.rfp.copilot.constants.Constants;
import com.rfp.copilot.prompt.PromptType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TorFilteringService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TorFilteringService.class);

    private final DocumentProcessingService documentService;
    private final LlmService llmService;

    public TorFilteringService(DocumentProcessingService documentService, LlmService llmService) {
        this.documentService = documentService;
        this.llmService = llmService;
    }

    public String analyzeTorSuitability(String sourceFilter) {

        String technicalRequirements = documentService.extractByPromptType(sourceFilter, PromptType.TECHNICAL);
        String timelineInfo = documentService.extractByPromptType(sourceFilter, PromptType.TIMELINE);
        String companyCapabilities = Constants.COMPANY_PROFILE_INFORMATION;
        logger.info("company capabilities: {}", companyCapabilities);

        Map<String, Object> analysisData = new HashMap<>();
        analysisData.put("technicalRequirements", technicalRequirements);
        analysisData.put("timelineInfo", timelineInfo);
        analysisData.put("companyCapabilities", companyCapabilities);

        String prompt = generateAnalysisPrompt(analysisData);
        return llmService.generateResponse(prompt);
    }

    public String generateAnalysisPrompt(Map<String, Object> analysisData) {
        return """
        Analyze the following information and provide a BidDecision (YES or NO) along with a reasoning:
    
        === TOR Technical Requirements ===
        %s
    
        === TOR Timeline Info ===
        %s
    
        === Company Profile and Capabilities ===
        %s
    
        Evaluate the following:
    
        1. Does the company demonstrate relevant expertise or adjacent experience for the key technologies and skills listed in the TOR (e.g., AI modules, LLM integration, ML engineering)?
        2. Has the company completed similar or related projects — even if not an exact match — that suggest capability in this domain?
        3. Are there any **major gaps** in the company profile that would make delivering this project unlikely?
        4. Does the proposed timeline seem achievable based on the company’s demonstrated past delivery capacity?
    
        Your decision should be pragmatic:
        - Recommend **YES** if the company shows clear or closely related experience for most requirements and no major capability gaps.
        - Recommend **NO** only if there are **significant shortcomings** that would prevent successful delivery.
    
        Your output MUST follow this format exactly:
    
        BidDecision: YES or NO  
        Reason: [A short, realistic justification based strictly on the data above.]
    
        Do not invent capabilities not found in the profile, but reasonable extrapolation from related experience is allowed.
        """.formatted(
                analysisData.get("technicalRequirements"),
                analysisData.get("timelineInfo"),
                analysisData.get("companyCapabilities")
        );
    }
}