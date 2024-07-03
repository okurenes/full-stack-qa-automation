package com.qa.automation.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;
import java.util.Properties;

public class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Properties config;

    private static final int IMPLICIT_WAIT = 10;
    private static final int EXPLICIT_WAIT = 15;
    private static final int PAGE_LOAD_TIMEOUT = 30;

    @BeforeSuite(alwaysRun = true)
    public void loadConfig() {
        config = new Properties();
        try {
            config.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (Exception e) {
            log.error("Failed to load config.properties", e);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = buildChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT));
        log.info("Browser session started");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            captureScreenshot();
            log.error("Test FAILED: {}", result.getName());
        }
        if (driver != null) {
            driver.quit();
            log.info("Browser session closed");
        }
    }

    private ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        String headless = System.getProperty("headless", "true");
        if ("true".equalsIgnoreCase(headless)) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-gpu",
            "--window-size=1920,1080",
            "--disable-extensions",
            "--disable-infobars"
        );
        return options;
    }

    @Attachment(value = "Screenshot on Failure", type = "image/png")
    public byte[] captureScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    protected String getBaseUrl() {
        return config.getProperty("base.url", "https://www.saucedemo.com");
    }
}
