package framework.automation.manager;

import framework.automation.utils.ConfigLoader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * Per-thread {@link WebDriver} using {@link ThreadLocal} for parallel Cucumber scenarios.
 * Selenium Manager resolves the ChromeDriver binary.
 */
@SuppressWarnings("null")
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    private static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("headless", Boolean.toString(ConfigLoader.isHeadless())));
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito", "--ignore-certificate-errors");
        if (isHeadless()) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        // GitHub Actions / Linux CI: small /dev/shm and sandbox constraints (avoids crashes / exit code 1)
        if ("true".equalsIgnoreCase(System.getenv("CI"))) {
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        }
        return options;
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            WebDriver driver = new ChromeDriver(chromeOptions());
            if ("true".equalsIgnoreCase(System.getenv("CI"))) {
                // Remote demo sites + GitHub network: avoid premature timeouts on navigation / async scripts
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(90));
                driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));
            }
            // Headless already sets viewport via --window-size; maximize() can be flaky on Linux CI
            if (!isHeadless()) {
                driver.manage().window().maximize();
            }
            DRIVER.set(driver);
        }
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
