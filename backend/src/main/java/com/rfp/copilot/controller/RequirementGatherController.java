package com.rfp.copilot.controller;

import com.rfp.copilot.service.RequirementGatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequirementGatherController {
    private final RequirementGatherService requirementGatherService;

    public RequirementGatherController(
            RequirementGatherService requirementGatherService
    ) {
        this.requirementGatherService = requirementGatherService;
    }

    @GetMapping("/gather-requirement")
    public String gatherRequirement(
            @RequestParam(value = "source", required = false) String sourceFilter
    ) {
        String result = requirementGatherService.analyzeTorSuitability(sourceFilter);
        System.out.println("response:" + result);
        return result;
    }

}
