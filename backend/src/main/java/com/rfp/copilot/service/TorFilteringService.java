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

        Carefully evaluate:
        1. Whether the company has demonstrable expertise in each of the key technologies and skills listed in the TOR (e.g., AI modules, LLM integration, ML engineering, etc.).
        2. Whether the company has completed similar projects in the past — especially in the AI/ML/LLM domain.
        3. If the company profile does NOT mention required experience, consider that a red flag.
        4. Whether the proposed timeline aligns with the project complexity and the company's historical delivery capabilities.
        5. Any critical gaps in domain expertise, technical skills, or delivery capacity.

        Your output MUST follow this format exactly:

        BidDecision: YES or NO  
        Reason: [A short, logical justification based strictly on the data above. If required experience is missing, explain that.]

        Do not infer capabilities beyond what's explicitly mentioned in the Company Profile.
        Be conservative in recommendation — only recommend YES if all key requirements are clearly matched.
        """.formatted(
                analysisData.get("technicalRequirements"),
                analysisData.get("timelineInfo"),
                analysisData.get("companyCapabilities")
        );
    }


//    public String generateAnalysisPrompt(Map<String, Object> analysisData) {
//        return """
//                TOR Technical Requirements:
//                %s
//
//                TOR Timeline Info:
//                %s
//
//                Company Profile:
//                %s
//
//                Based on the technical requirements, timeline, and company capabilities,
//                determine whether the company should bid on this TOR. Consider:
//                - Does the company have sufficient expertise in the required technologies?
//                - Is the proposed project timeline realistic based on project complexity?
//                - Are any critical skills or domain experiences missing?
//
//                Respond with a BidDecision (YES or NO) and short reasoning.
//                """.formatted(
//                analysisData.get("technicalRequirements"),
//                analysisData.get("timelineInfo"),
//                analysisData.get("companyCapabilities")
//        );
//    }

}