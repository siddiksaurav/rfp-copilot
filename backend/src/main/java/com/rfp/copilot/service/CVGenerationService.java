package com.rfp.copilot.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.rfp.copilot.entity.Employee;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class CVGenerationService {
    private final ChatModel chatModel;
    private final EmployeeSelectionService employeeSelectionService;

    public CVGenerationService(ChatModel chatModel,
                               EmployeeSelectionService employeeSelectionService) {
        this.chatModel = chatModel;
        this.employeeSelectionService = employeeSelectionService;
    }

    public List<String> generateCv() {
        List<Employee> employees = employeeSelectionService.fetchEmployees();
        StringBuilder combinedCvs = new StringBuilder();

        for (Employee employee : employees) {
            String prompt = String.format("""
            Create a professional CV based on the following information. No need to use any extra info. Use only the available content. Remove markup tag:
            Name: %s
            Phone: %s
            Email: %s
            Location: %s
            Position: %s
            Academic Qualification: %s
            Subject: %s
            Experience: %s
            Skills: %s
            Certification: %s
            Experience Details: %s
            """,
                    employee.getName(),
                    employee.getPhone(),
                    employee.getEmail(),
                    employee.getLocation(),
                    employee.getPosition(),
                    employee.getAcademicQualification(),
                    employee.getSubject(),
                    employee.getExperience(),
                    employee.getSkills(),
                    employee.getCertification(),
                    employee.getExperienceDetails()
            );

            String response = chatModel.call(prompt);
            combinedCvs.append(response).append("\n\n---------------------------------------------\n\n");
        }
        // Generate a single PDF with all CVs
        if (!combinedCvs.isEmpty()) {
            String fileName = "selective_employees_cv.pdf";
            saveCvAsPdf(combinedCvs.toString(), fileName);
        }

        return List.of(combinedCvs.toString());
    }

    private void saveCvAsPdf(String cvContent, String fileName) {
        try {
            String dest = "output/" + fileName;
            File file = new File(dest);
            file.getParentFile().mkdirs();

            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.setMargins(30, 30, 30, 30);

            PdfFont font = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);
            PdfFont fontBold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);

            String[] lines = cvContent.split("\\r?\\n");

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                int colonIndex = line.indexOf(":");
                if (colonIndex != -1) {
                    String label = line.substring(0, colonIndex + 1).trim(); // includes ':'
                    String value = line.substring(colonIndex + 1).trim();

                    Text labelText = new Text(label + " ")
                            .setFont(fontBold)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK);

                    Text valueText = new Text(value)
                            .setFont(font)
                            .setFontSize(12);

                    Paragraph paragraph = new Paragraph()
                            .add(labelText)
                            .add(valueText)
                            .setMarginBottom(3)
                            .setTextAlignment(TextAlignment.LEFT);

                    document.add(paragraph);
                } else {
                    // fallback if no colon is found
                    Paragraph paragraph = new Paragraph(new Text(line)
                            .setFont(font)
                            .setFontSize(12))
                            .setMarginBottom(3);
                    document.add(paragraph);
                }
            }

            document.close();
            System.out.println("CV saved as PDF: " + dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
