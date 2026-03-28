package framework.automation.hooks;

import framework.automation.manager.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

/**
 * ConfigurationTestHook is responsible for setting up and tearing down the test environment
 * for each Cucumber scenario.
 * <p>
 * It initializes the WebDriver before each scenario and, on failure, attaches a screenshot to Allure.
 * Finally, it closes the WebDriver instance.
 */
public class ConfigurationTestHook {
    private final TestContext testContext;

    /**
     * Constructor that initializes the test context.
     *
     * @param testContext The shared test context for managing scenario data.
     */
    public ConfigurationTestHook(TestContext testContext) {
        this.testContext = testContext;
    }

    /**
     * This method is executed before each scenario starts.
     * <p>
     * It sets the scenario in the test context and initializes the WebDriver instance.
     *
     * @param scenario The current running scenario.
     */
    @Before
    public void setUp(Scenario scenario) {
        testContext.setScenario(scenario);
        DriverManager.getDriver();
    }

    /**
     * This method is executed after each scenario ends.
     * <p>
     * If the scenario fails, captures a screenshot and attaches it to Allure. Then closes the WebDriver.
     */
    @After
    public void tearDown() {
        Scenario scenario = testContext.getScenario();

        if (scenario.isFailed()) {
            WebDriver driver = DriverManager.getDriver();
            if (driver instanceof TakesScreenshot takesScreenshot) {
                byte[] png = takesScreenshot.getScreenshotAs(OutputType.BYTES);
                String fileName = pngFileName(scenario.getName());
                Allure.addAttachment(fileName, "image/png", new ByteArrayInputStream(png), "png");
            }
        }
        DriverManager.quitDriver();
    }

    /**
     * File name: scenario name plus {@code .png} extension (path-like characters sanitized).
     */
    private static String pngFileName(String scenarioName) {
        String base = (scenarioName == null || scenarioName.isBlank()) ? "scenario" : scenarioName.trim();
        String sanitized = base.replaceAll("[\\\\/:*?\"<>|\\r\\n]", "_");
        return sanitized.endsWith(".png") ? sanitized : sanitized + ".png";
    }
}
