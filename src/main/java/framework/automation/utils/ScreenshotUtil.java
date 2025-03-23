package framework.automation.utils;

import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Utility class for taking screenshots.
 */
public class ScreenshotUtil {
    /**
     * Takes a screenshot and returns it as a byte array.
     *
     * @param driver The WebDriver instance.
     * @return The screenshot as a byte array.
     */
    public static byte[] takeScreenshot(WebDriver driver) {
        if (driver instanceof TakesScreenshot) {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        }
        return new byte[0];
    }

    /**
     * Takes a screenshot and attaches it to the Cucumber scenario.
     *
     * @param driver   The WebDriver instance.
     * @param scenario The current Cucumber scenario.
     * @param stepName The name or description of the step.
     */
    public static void captureScreenshot(WebDriver driver, Scenario scenario, String stepName) {
        byte[] screenshot = takeScreenshot(driver);
        scenario.attach(screenshot, "image/png", stepName);
    }
}
