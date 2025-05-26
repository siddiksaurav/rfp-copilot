package com.rfp.copilot.controller;

import com.rfp.copilot.service.TenderScraperService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenders")
public class TenderController {

    private final TenderScraperService scraperService;

    public TenderController(TenderScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @GetMapping("/nature-and-title")
    public List<Map<String, String>> getTenderNatureAndTitle() {
        return scraperService.scrapeTenderNatureAndTitle();
    }

    @GetMapping("/download")
    public void downloadTenderPDF () {
        scraperService.downloadAllTenderPDFs ();
    }
}

