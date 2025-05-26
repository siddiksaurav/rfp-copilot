package com.rfp.copilot.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TenderScraperService {

    private WebDriver driver;
    private WebDriverWait wait;
    private String downloadPath;

    @PostConstruct
    public void initialize() {
        WebDriverManager.chromedriver().setup();

        // Set up a download directory
        downloadPath = System.getProperty("user.dir") + "/downloads";
        new File(downloadPath).mkdirs(); // Create directory if it doesn't exist

        ChromeOptions options = new ChromeOptions();

        // Configure download settings
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadPath);
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("plugins.always_open_pdf_externally", true);
        prefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", prefs);

        // Other Chrome options
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-popup-blocking");
        // Uncomment for headless mode if needed
        // options.addArguments("--headless");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @PreDestroy
    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }

    public List<Map<String, String>> scrapeTenderNatureAndTitle() {
        List<Map<String, String>> tenderData = new ArrayList<>();

        try {
            // Navigate to the tender search page
            driver.get("https://www.eprocure.gov.bd/resources/common/StdTenderSearch.jsp?h=t");

            // Wait for the page to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
            Thread.sleep(3000); // Additional wait for dynamic content

            // Find all tender forms
            List<WebElement> tenderForms = driver.findElements(By.xpath("//form[contains(@name, 'viewtenderform')]"));

            System.out.println("Found " + tenderForms.size() + " tender forms");

            for (int i = 0; i < tenderForms.size(); i++) {
                try {
                    WebElement form = tenderForms.get(i);
                    String formId = form.getAttribute("id");
                    String formName = form.getAttribute("name");

                    // Get tender ID from hidden input
                    String tenderId = "";
                    try {
                        WebElement idInput = form.findElement(By.xpath(".//input[@name='id']"));
                        tenderId = idInput.getAttribute("value");
                    } catch (Exception e) {
                        // Continue if ID not found
                    }

                    // Get tender title from the span element
                    String tenderTitle = "";
                    try {
                        WebElement titleSpan = form.findElement(By.xpath(".//span[contains(@id, 'tenderBrief')]"));
                        tenderTitle = titleSpan.getText().trim();
                    } catch (Exception e) {
                        try {
                            // Alternative: get from any text content in the form
                            tenderTitle = form.getText().trim();
                        } catch (Exception ex) {
                            tenderTitle = "Tender " + i;
                        }
                    }

                    Map<String, String> rowData = new HashMap<>();
                    rowData.put("formId", formId);
                    rowData.put("formName", formName);
                    rowData.put("tenderId", tenderId);
                    rowData.put("tenderTitle", tenderTitle);
                    rowData.put("formIndex", String.valueOf(i));

                    tenderData.add(rowData);

                } catch (Exception e) {
                    System.err.println("Error processing form " + i + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Error scraping tender data: " + e.getMessage());
            e.printStackTrace();
        }

        return tenderData;
    }

    public boolean downloadTenderPDF(String formId, String tenderTitle) {
        String originalWindow = driver.getWindowHandle();

        try {
            System.out.println("Downloading PDF for: " + tenderTitle);

            // Find the form by ID and submit it
            WebElement form = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(formId)));

            // Submit the form using JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.getElementById(arguments[0]).submit();", formId);

            // Wait for new window/tab to open
            Thread.sleep(3000);

            // Switch to the new window
            Set<String> allWindows = driver.getWindowHandles();
            for (String windowHandle : allWindows) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            // Wait for the tender details page to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            Thread.sleep(3000); // Wait for dynamic content

            // Scroll to bottom to ensure PDF button is visible
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(2000);

            // Try to find and click the PDF button
            WebElement pdfButton = findPDFButton();

            if (pdfButton != null) {
                System.out.println("Found PDF button, clicking...");

                // Click using JavaScript to avoid interception issues
                js.executeScript("arguments[0].click();", pdfButton);

                // Wait for download to complete
                Thread.sleep(5000);

                System.out.println("PDF download initiated for: " + tenderTitle);

                // Close the new window
                driver.close();
                driver.switchTo().window(originalWindow);

                return true;
            } else {
                System.out.println("PDF button not found for: " + tenderTitle);
                // Close the new window
                driver.close();
                driver.switchTo().window(originalWindow);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error downloading PDF for " + tenderTitle + ": " + e.getMessage());
            e.printStackTrace();

            // Try to close any open windows and return to original
            try {
                Set<String> allWindows = driver.getWindowHandles();
                for (String windowHandle : allWindows) {
                    if (!windowHandle.equals(originalWindow)) {
                        driver.switchTo().window(windowHandle);
                        driver.close();
                    }
                }
                driver.switchTo().window(originalWindow);
            } catch (Exception ex) {
                // If window management fails, navigate back to main page
                driver.get("https://www.eprocure.gov.bd/resources/common/StdTenderSearch.jsp?h=t");
            }

            return false;
        }
    }

    public void downloadAllTenderPDFs() {
        // First, get all tender data
        List<Map<String, String>> tenderData = scrapeTenderNatureAndTitle();

        System.out.println("Found " + tenderData.size() + " tenders to process");

        for (Map<String, String> tender : tenderData) {
            String formId = tender.get("formId");
            String tenderTitle = tender.get("tenderTitle");

            if (formId != null && !formId.isEmpty()) {
                downloadTenderPDF(formId, tenderTitle);

                // Wait between downloads to be respectful to the server
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private WebElement findPDFButton() {
        // Multiple selectors to find the PDF button
        String[] pdfButtonSelectors = {
                "//input[@value='Save as PDF']",
                "//input[contains(@value, 'PDF')]",
                "//input[contains(@value, 'pdf')]",
                "//button[contains(text(), 'Save as PDF')]",
                "//button[contains(text(), 'PDF')]",
                "//button[contains(text(), 'pdf')]",
                "//a[contains(text(), 'Save as PDF')]",
                "//a[contains(text(), 'PDF')]",
                "//a[contains(text(), 'pdf')]",
                "//input[@type='button' and contains(@onclick, 'pdf')]",
                "//input[@type='button' and contains(@onclick, 'PDF')]",
                "//input[@type='submit' and contains(@value, 'PDF')]",
                "//input[@type='submit' and contains(@value, 'pdf')]",
                "//*[contains(text(), 'Save as PDF')]",
                "//*[contains(text(), 'Download PDF')]",
                "//*[contains(text(), 'Export PDF')]"
        };

        for (String selector : pdfButtonSelectors) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(selector));
                for (WebElement element : elements) {
                    if (element.isDisplayed() && element.isEnabled()) {
                        System.out.println("Found PDF button with selector: " + selector);
                        return element;
                    }
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }

        // If no PDF button found, print page source for debugging
        System.out.println("PDF button not found. Page source contains:");
        try {
            String pageSource = Objects.requireNonNull(driver.getPageSource()).toLowerCase();
            if (pageSource.contains("pdf")) {
                System.out.println("Page contains 'pdf' text");
            }
            if (pageSource.contains("save")) {
                System.out.println("Page contains 'save' text");
            }
            if (pageSource.contains("download")) {
                System.out.println("Page contains 'download' text");
            }
        } catch (Exception e) {
            System.err.println("Error checking page source: " + e.getMessage());
        }

        return null;
    }

    public boolean downloadSpecificTenderPDF(int formIndex) {
        try {
            // Navigate to main page first
            driver.get("https://www.eprocure.gov.bd/resources/common/StdTenderSearch.jsp?h=t");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
            Thread.sleep(3000);

            // Find all tender forms
            List<WebElement> tenderForms = driver.findElements(By.xpath("//form[contains(@name, 'viewtenderform')]"));

            if (formIndex >= 0 && formIndex < tenderForms.size()) {
                WebElement form = tenderForms.get(formIndex);
                String formId = form.getAttribute("id");

                // Get tender title
                String tenderTitle = "";
                try {
                    WebElement titleSpan = form.findElement(By.xpath(".//span[contains(@id, 'tenderBrief')]"));
                    tenderTitle = titleSpan.getText().trim();
                } catch (Exception e) {
                    tenderTitle = "Tender " + formIndex;
                }

                return downloadTenderPDF(formId, tenderTitle);
            } else {
                System.out.println("Invalid form index: " + formIndex + ". Available forms: 0-" + (tenderForms.size() - 1));
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error downloading specific tender PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean downloadTenderPDFByClickingLink(int formIndex) {
        try {
            // Navigate to main page first
            driver.get("https://www.eprocure.gov.bd/resources/common/StdTenderSearch.jsp?h=t");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
            Thread.sleep(3000);

            // Find all tender forms
            List<WebElement> tenderForms = driver.findElements(By.xpath("//form[contains(@name, 'viewtenderform')]"));

            if (formIndex >= 0 && formIndex < tenderForms.size()) {
                WebElement form = tenderForms.get(formIndex);

                // Get tender title
                String tenderTitle = "";
                try {
                    WebElement titleSpan = form.findElement(By.xpath(".//span[contains(@id, 'tenderBrief')]"));
                    tenderTitle = titleSpan.getText().trim();
                } catch (Exception e) {
                    tenderTitle = "Tender " + formIndex;
                }

                System.out.println("Clicking tender link for: " + tenderTitle);

                // Find and click the anchor tag within the form
                WebElement tenderLink = form.findElement(By.xpath(".//a[@onclick]"));

                String originalWindow = driver.getWindowHandle();

                // Click the link
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", tenderLink);

                // Wait for new window/tab to open
                Thread.sleep(3000);

                // Switch to the new window
                Set<String> allWindows = driver.getWindowHandles();
                for (String windowHandle : allWindows) {
                    if (!windowHandle.equals(originalWindow)) {
                        driver.switchTo().window(windowHandle);
                        break;
                    }
                }

                // Wait for the tender details page to load
                wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
                Thread.sleep(3000);

                // Scroll to bottom
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(2000);

                // Find and click PDF button
                WebElement pdfButton = findPDFButton();

                if (pdfButton != null) {
                    System.out.println("Found PDF button, clicking...");
                    js.executeScript("arguments[0].click();", pdfButton);
                    Thread.sleep(5000);
                    System.out.println("PDF download initiated for: " + tenderTitle);

                    // Close the new window
                    driver.close();
                    driver.switchTo().window(originalWindow);

                    return true;
                } else {
                    System.out.println("PDF button not found for: " + tenderTitle);
                    driver.close();
                    driver.switchTo().window(originalWindow);
                    return false;
                }

            } else {
                System.out.println("Invalid form index: " + formIndex);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error downloading PDF by clicking link: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public List<String> getDownloadedFiles() {
        File downloadDir = new File(downloadPath);
        if (downloadDir.exists() && downloadDir.isDirectory()) {
            return Arrays.stream(Objects.requireNonNull(downloadDir.listFiles()))
                    .filter(File::isFile)
                    .map(File::getName)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}