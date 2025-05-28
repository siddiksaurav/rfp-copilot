package com.rfp.copilot.controller;

import com.rfp.copilot.service.CVGenerationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CVGenerationController {
    private final CVGenerationService cvGenerationService;

    public CVGenerationController(CVGenerationService cvGenerationService) {
        this.cvGenerationService = cvGenerationService;
    }

    @GetMapping("/generate-cv")
    public List<String> generateCV (@RequestParam(value = "source", defaultValue = "Dummy") String sourceFilter) {

        return cvGenerationService.generateCv(sourceFilter);
    }
}
