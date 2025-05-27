package com.rfp.copilot.controller;

import com.rfp.copilot.service.TorFilteringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TorFilteringController {
    private static final Logger logger = LoggerFactory.getLogger(TorFilteringController.class);
    private final TorFilteringService torFilteringService;

    public TorFilteringController(TorFilteringService torFilteringService) {
        this.torFilteringService = torFilteringService;
    }

    @GetMapping("/filter")
    public String simplify(
            @RequestParam(value = "question", defaultValue = "Give a welcome message") String question,
            @RequestParam(value = "source", required = false) String sourceFilter
    ) {
        logger.info("starting tor filtering for source: {}", sourceFilter);

        String result = torFilteringService.analyzeTorSuitability(sourceFilter);
        logger.info("final tor filtering: {}", result);

        return result;
    }

}
