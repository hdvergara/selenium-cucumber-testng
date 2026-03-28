package framework.automation.manager;

import framework.automation.utils.ConfigLoader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Per-thread {@link WebDriver} using {@link ThreadLocal} for parallel Cucumber scenarios.
 * Selenium Manager resolves the ChromeDriver binary.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito", "--ignore-certificate-errors");
        if (Boolean.parseBoolean(System.getProperty("headless", Boolean.toString(ConfigLoader.isHeadless())))) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        return options;
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            WebDriver driver = new ChromeDriver(chromeOptions());
            driver.manage().window().maximize();
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
