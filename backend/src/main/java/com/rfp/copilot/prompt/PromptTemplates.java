package com.rfp.copilot.prompt;

public class PromptTemplates {

    public static String getTimeLineInfoPrompt() {
        return """
                What is the total project timeline including development and testing? Specify the duration mentioned in 
                weeks or months. Focus on any deadlines or timeframes given for software development and testing phases 
                in the TOR
        """;
    }

    public static String getTechnicalInfoPrompt() {
        return """
            What are the required technical stacks, tools, or domain expertise mentioned in the TOR? Look for specific 
            technologies, AI components (e.g., chatbot, LLM), programming languages, or industry-specific knowledge needed to 
            complete the project.
        """;
    }

    public static String generateRequirementGatherPrompt() {
        return """
           Retrieve all relevant parts of the RFP that contain:

           - Required documents or submission formats
           - Technical specifications or implementation details
           - Project timeline, milestones, or deadlines
           - Evaluation and scoring criteria
           - Legal, compliance, or administrative conditions
           - Any section that is referenced by others (e.g., “See Section 5”, “Refer to PDS”)

           Prioritize sections that contain actionable proposal-related instructions.
           """;
    }

    public static String getWorkSchedulePrompt() {
        return """
                Based on the Terms of Reference in the RFP, extract all mentioned software modules, components, and key functional features required for the proposed solution. Focus only on technical deliverables and grouped features if listed.
        """;
    }

    public static String getRequirementChatPrompt() {
        return """
            You are a good assistant who has access to RAG database of Term of Reference. Provide a detailed response to the user question:
        """;
    }

    public static String generateExperienceAndTeamCompositionPrompt() {
        return """
               - Spring boot, UI/UX, Fullstack, Tester, Mobile Developer
               - Professional experience of the organization or team members. Professional staff qualifications and competence
               - Role definitions and responsibilities"
               - Years of experience required
               - Composition of key personnel (e.g., Project Manager, Full Stack Developer, Software Engineer, iOS Developer, Tester)
               - Any prior relevant project experience or case studies.
           """;
    }

    public static String getRequirementChatPrompt() {
        return """
            You are a good assistant who has access to RAG database of Term of Reference. Provide a detailed response to the user question:
        """;
    }


}
