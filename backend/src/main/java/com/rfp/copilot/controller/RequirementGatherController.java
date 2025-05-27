package com.rfp.copilot.controller;

import com.rfp.copilot.service.RequirementGatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequirementGatherController {
    private static final Logger logger = LoggerFactory.getLogger(RequirementGatherController.class);

    private final RequirementGatherService requirementGatherService;

    public RequirementGatherController(RequirementGatherService requirementGatherService) {
        this.requirementGatherService = requirementGatherService;
    }

    @GetMapping("/gather-requirement")
    public String gatherRequirement(@RequestParam(value = "source", required = false) String sourceFilter) {
        logger.info("starting requirement gathering for source: {}", sourceFilter);

        String result = requirementGatherService.analyzeTorSuitability(sourceFilter);
        logger.info("final requirement gather: {}", result);

        return result;
    }

}
