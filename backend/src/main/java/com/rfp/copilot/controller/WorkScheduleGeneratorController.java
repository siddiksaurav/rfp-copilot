package com.rfp.copilot.controller;

import com.rfp.copilot.service.WorkScheduleGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkScheduleGeneratorController {
    private static final Logger logger = LoggerFactory.getLogger(WorkScheduleGeneratorController.class);
    private final WorkScheduleGeneratorService workScheduleGeneratorService;

    public WorkScheduleGeneratorController(WorkScheduleGeneratorService workScheduleGeneratorService) {
        this.workScheduleGeneratorService = workScheduleGeneratorService;
    }

    @GetMapping("/work-schedule/generate")
    public String simplify(
            @RequestParam(value = "question", defaultValue = "Give a welcome message") String question,
            @RequestParam(value = "source", required = false) String sourceFilter
    ) {
        String result = workScheduleGeneratorService.createWorkSchedule(sourceFilter);
        logger.info("final work-schedule generate: {}", result);

        return result;
    }

}
