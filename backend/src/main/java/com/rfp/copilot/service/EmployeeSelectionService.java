package com.rfp.copilot.service;

import com.rfp.copilot.entity.Employee;
import com.rfp.copilot.prompt.PromptTemplates;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeSelectionService {
    private final VectorStore vectorStore;
    private final SchemaService schemaService;
    private final ChatModel chatModel;
    private final EntityManager entityManager;

    public EmployeeSelectionService(VectorStore vectorStore,
                                    SchemaService schemaService,
                                    ChatModel chatModel,
                                    EntityManager entityManager) {
        this.vectorStore = vectorStore;
        this.schemaService = schemaService;
        this.chatModel = chatModel;
        this.entityManager = entityManager;
    }

    public String extractExperiencesFromTor(String sourceFilter, String prompt) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(prompt)
                .topK(5)
                .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);

        return Optional.ofNullable(documents)
                .orElse(List.of())
                .stream()
                .filter(doc -> sourceFilter.equals(doc.getMetadata().get("source")))
                .map(Document::getFormattedContent)
                .collect(Collectors.joining());
    }

    public List<Employee> fetchEmployees(String sourceFilter) {
        String experiences = extractExperiencesFromTor(sourceFilter, PromptTemplates.generateExperienceAndTeamCompositionPrompt());

        System.out.println("########### Experiences ############");
        System.out.println(experiences);

        String schema = schemaService.getSchemaDescription();
        String prompt = """
        You are an SQL expert assistant that converts natural language questions into SQL queries. Please help me to fetch Employee with matching experiences
        DATABASE SCHEMA:
        %s
        INSTRUCTIONS:
        1. Find similarities in skills and experience_details columns.
        2. If the question cannot be answered using the available schema (missing tables or columns), respond with exactly "NO_RELEVANT_DATA_AVAILABLE" and nothing else.
        3. Return ONLY the raw SQL query with no explanations, markdown formatting, or comments.
        4. Do not use any tables or columns that aren't defined in the schema.
        5. Use appropriate WHERE clauses to filter data when needed.
        6. Use ORDER BY, GROUP BY, or aggregation functions when appropriate.
        7. Fetch full employee entity
        USER QUESTION: "%s"
    """.formatted(schema, experiences);

        String query = chatModel.call(prompt);

        System.out.println("############## Query ##############");
        System.out.println(query);

        return getEmployeesWithMatchingExperience(query);
    }


    public List<Employee> getEmployeesWithMatchingExperience(String query) {
        Query nativeQuery = entityManager.createNativeQuery(query, Employee.class);

        return nativeQuery.getResultList();
    }


}
