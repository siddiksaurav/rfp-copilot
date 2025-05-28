package com.rfp.copilot.controller;

import com.rfp.copilot.service.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProcessController {
    private static final Logger logger = LoggerFactory.getLogger(TorFilteringController.class);
    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping("/tor-process-queue")
    public List<String> torProcessQueue() {
        logger.info("getting tor process queue");
        return processService.torProcessQueue();
    }

    @GetMapping("/start-process")
    public String startProcess(@RequestParam String sourceName) {
        logger.info("starting process for source: {}", sourceName);
        return processService.processPdf(sourceName);
    }
}
