package com.rfp.copilot.dto;

import lombok.Data;

@Data
public class SchemaDefinition {
    private String name;
    private TableDefinition[] tables;
}
