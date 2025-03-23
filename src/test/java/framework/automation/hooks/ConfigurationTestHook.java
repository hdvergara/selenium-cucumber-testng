package framework.automation.hooks;

import framework.automation.manager.DriverManager;
import framework.automation.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * ConfigurationTestHook is responsible for setting up and tearing down the test environment
 * for each Cucumber scenario.
 * <p>
 * It initializes the WebDriver before each scenario and ensures that a screenshot is taken
 * if a scenario fails. Finally, it closes the WebDriver instance.
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
     * If the scenario fails, it captures a screenshot. Then, it closes the WebDriver instance.
     */
    @After
    public void tearDown() {
        Scenario scenario = testContext.getScenario();

        if (scenario.isFailed()) {
            ScreenshotUtil.captureScreenshot(DriverManager.getDriver(), scenario, "Failure Screenshot");
        }
        DriverManager.quitDriver();
    }
}
