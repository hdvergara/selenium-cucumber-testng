package framework.automation.manager;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Manages the WebDriver instance for the test execution.
 * Implements a Singleton pattern to ensure only one driver instance is created.
 */
public class DriverManager {

    private static WebDriver driver;

    /**
     * Private constructor to prevent instantiation of the class.
     */
    private DriverManager() {
    }

    /**
     * Returns the current WebDriver instance, initializing it if necessary.
     * Configures ChromeDriver with optional headless mode and certificate error handling.
     *
     * @return The WebDriver instance.
     */
    public static WebDriver getDriver() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            WebDriverManager.chromedriver().setup();
            options.addArguments("--incognito");
            if (System.getProperty("headless", "false").equals("true")) {
                options.addArguments("--headless");
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
            }
            options.addArguments("--ignore-certificate-errors");
            driver = new ChromeDriver(options);
            driver.manage().window().maximize();
        }
        return driver;
    }

    /**
     * Closes the WebDriver instance and releases resources.
     * Sets the driver instance to null.
     */
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
