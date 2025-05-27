package com.rfp.copilot.service;

import com.rfp.copilot.entity.Employee;
import com.rfp.copilot.repository.EmployeeRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CVGenerationService {
    private final EmployeeRepository employeeRepository;
    private final ChatModel chatModel;

    public CVGenerationService(EmployeeRepository employeeRepository,
                               ChatModel chatModel) {
        this.employeeRepository = employeeRepository;
        this.chatModel = chatModel;
    }
    public String generateCv () {
        Employee employee = employeeRepository.findAll().get(0);

        String prompt = String.format("""
            Create a professional CV based on the following information:
            Name: %s
            Email: %s
            Position: %s
            Experience: %s
            Skills: %s
            Education: %s
            """,
                employee.getName(),
                employee.getSubject(),
                employee.getPosition(),
                employee.getExperience(),
                employee.getSkills(),
                employee.getAcademicQualification()
        );

        String response = chatModel.call(prompt);

        return response;
    }
}
