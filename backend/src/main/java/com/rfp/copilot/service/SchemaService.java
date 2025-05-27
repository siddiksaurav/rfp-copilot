package com.rfp.copilot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rfp.copilot.dto.SchemaDefinition;
import com.rfp.copilot.dto.TableDefinition;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchemaService {


    private String schemaName = "public";
    private final Map<String, String> schemaCache = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DEFAULT_SCHEMA = """
        {
          "name": "public",
          "tables": [
            {
              "name": "employees",
              "columns": [
                { "name": "id", "type": "BIGSERIAL", "primary_key": true },
                { "name": "position", "type": "VARCHAR(255)" },
                { "name": "name", "type": "VARCHAR(255)" },
                { "name": "phone", "type": "VARCHAR(255)" },
                { "name": "email", "type": "VARCHAR(255)" },
                { "name": "location", "type": "VARCHAR(255)" },
                { "name": "academic_qualification", "type": "VARCHAR(255)" },
                { "name": "subject", "type": "VARCHAR(255)" },
                { "name": "experience", "type": "INTEGER" },
                { "name": "skills", "type": "VARCHAR(255)" },
                { "name": "certification", "type": "VARCHAR(255)" },
                { "name": "experience_details", "type": "TEXT" }
              ]
            }
          ]
        }
    """;

    @PostConstruct
    public void init() {
        try {
            // Load schema definitions from resources/schemas directory
            Resource schemasDir = new ClassPathResource("schemas");
            if (schemasDir.exists()) {
                Files.walk(Paths.get(schemasDir.getURI()))
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".json"))
                        .forEach(this::loadSchema);
            }
        } catch (IOException e) {
            schemaCache.put("default", DEFAULT_SCHEMA);
        }

        if (!schemaCache.containsKey("default")) {
            schemaCache.put("default", DEFAULT_SCHEMA);
        }
    }

    private void loadSchema(java.nio.file.Path path) {
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            SchemaDefinition definition = objectMapper.readValue(content, SchemaDefinition.class);

            StringBuilder schemaDesc = new StringBuilder();
            List<TableDefinition> tables = List.of(definition.getTables());
            for (TableDefinition table : tables) {
                schemaDesc.append(table.getName())
                        .append("(")
                        .append(String.join(", ", table.getColumns()))
                        .append(")\n");
            }

            schemaCache.put(definition.getName(), schemaDesc.toString().trim());

        } catch (IOException e) {
        }
    }

    public String getSchemaDescription() {
        if (schemaName == null || !schemaCache.containsKey(schemaName)) {
            return schemaCache.get("default");
        }
        return schemaCache.get(schemaName);
    }
}
