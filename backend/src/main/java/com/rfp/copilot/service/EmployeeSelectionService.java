package com.rfp.copilot.service;

import com.rfp.copilot.entity.Employee;
import com.rfp.copilot.prompt.PromptTemplates;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeSelectionService {
    private final VectorStore vectorStore;
    private final SchemaService schemaService;
    private final ChatModel chatModel;
    private final EntityManager entityManager;
    private final JdbcClient jdbcClient;

    public EmployeeSelectionService(
            VectorStore vectorStore,
            SchemaService schemaService,
            ChatModel chatModel,
            EntityManager entityManager,
            JdbcClient jdbcClient
    ) {
        this.vectorStore = vectorStore;
        this.schemaService = schemaService;
        this.chatModel = chatModel;
        this.entityManager = entityManager;
        this.jdbcClient = jdbcClient;
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
                    You are an SQL expert assistant that converts natural language questions into SQL queries. Please help me to fetch Employee with matching experiences.
                
                    DATABASE SCHEMA:
                    %s
                
                    INSTRUCTIONS:
                    1. Match similarities in the skills and experience_details columns using case-insensitive comparison.
                    2. If the question cannot be answered using the available schema (missing tables or columns), respond with exactly "NO_RELEVANT_DATA_AVAILABLE".
                    3. Return only the raw SQL query — no explanations, no comments, no markdown, no code blocks.
                    4. Use only the tables and columns explicitly defined in the schema.
                    5. Apply appropriate WHERE clauses for filtering.
                    6. Use GROUP BY, ORDER BY, or aggregation functions where applicable.
                    7. Return the full employee entity from the `employees` table.
                    8. Ensure logical conditions use correct operator precedence with parentheses.
                    9. Prioritize clean, readable SQL.
                
                    USER QUESTION: "%s"
                """.formatted(schema, experiences);


        String query = chatModel.call(prompt);

        System.out.println("############## Query ##############");
        System.out.println(query);

//        return getEmployeesWithMatchingExperience(query);
        var res = getEmployeesWithJdbcClient(query);
        var list = new ArrayList<Employee>();
        list.addAll(res);
        list.addAll(res);
        list.addAll(res);
        System.out.println(res.size() + " employees found");
        return list;
    }


    public List<Employee> getEmployeesWithMatchingExperience(String query) {
        Query nativeQuery = entityManager.createNativeQuery(query, Employee.class);
        return nativeQuery.getResultList();
    }

    public List<Employee> getEmployeesWithJdbcClient(String query) {
        return jdbcClient.sql(query)
                .query((rs, rowNum) -> {
                    Employee employee = new Employee();
                    employee.setId(rs.getLong("id"));
                    employee.setPosition(rs.getString("position"));
                    employee.setName(rs.getString("name"));
                    employee.setPhone(rs.getString("phone"));
                    employee.setEmail(rs.getString("email"));
                    employee.setLocation(rs.getString("location"));
                    employee.setAcademicQualification(rs.getString("academic_qualification"));
                    employee.setSubject(rs.getString("subject"));
                    employee.setExperience(rs.getInt("experience"));
                    employee.setSkills(rs.getString("skills"));
                    employee.setCertification(rs.getString("certification"));
                    employee.setExperienceDetails(rs.getString("experience_details"));
                    return employee;
                })
                .list();
    }

}
