package com.rfp.copilot.service;

import com.rfp.copilot.constants.Constants;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WorkScheduleGeneratorService {
    private final DocumentProcessingService documentService;
    private final LlmService llmService;

    public WorkScheduleGeneratorService(DocumentProcessingService documentService, LlmService llmService) {
        this.documentService = documentService;
        this.llmService = llmService;
    }
    public String createWorkSchedule(String sourceFilter) {
        String moduleInformation = documentService.extractFeatureInformation(sourceFilter);
        String previousWorkSchedule = Constants.WORK_SCHEDULE_DATA;

        Map<String, Object> analysisData = new HashMap<>();
        analysisData.put("moduleInformation", moduleInformation);
        analysisData.put("previousWorkSchedule", previousWorkSchedule);

        String prompt = generateWorkScheduleGeneratePrompt(analysisData);

        return llmService.generateResponse(prompt);
    }

    public static String generateWorkScheduleGeneratePrompt(Map<String, Object> analysisData) {
        return """
        Below is a list of modules and features extracted from a Terms of Reference (TOR). 
        Some parts may contain extra or irrelevant information due to automatic retrieval — please focus only on relevant technical modules and features.

        Extracted Modules/Features:
        %s

        Historical Work Schedule Reference (if applicable):
        %s

        Based on the above, generate a detailed work schedule or implementation timeline. 
        If historical schedule data is not sufficient or not relevant to the features mentioned, use standard industry estimates for building similar software modules.

        Output should:
        - Include phases (e.g., Planning, Design, Development, Testing, Deployment)
        - Assign estimated time duration for each module/phase
        - Present a logical order of execution
        - Mention assumptions if any were made

        Return the schedule in a readable format.
        """.formatted(
                analysisData.get("moduleInformation"),
                analysisData.get("previousWorkSchedule")
        );
    }

}
