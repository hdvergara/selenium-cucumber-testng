package framework.automation.hooks;

import io.cucumber.java.Scenario;
import lombok.Getter;
import lombok.Setter;

/**
 * TestContext is a container for storing contextual information during test execution.
 * <p>
 * It holds the current Cucumber scenario, which can be accessed and modified during test execution.
 * This class uses Lombok annotations to generate getters and setters automatically.
 */
@Setter
@Getter
public class TestContext {
    /**
     * The current Cucumber scenario.
     */
    private Scenario scenario;

}