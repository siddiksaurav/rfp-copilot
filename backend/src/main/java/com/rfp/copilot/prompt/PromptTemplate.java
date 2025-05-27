package com.rfp.copilot.prompt;

public class PromptTemplate {

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

}
