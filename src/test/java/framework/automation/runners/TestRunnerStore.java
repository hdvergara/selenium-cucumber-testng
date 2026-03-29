package framework.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/java/resources/features/",
        glue = {"framework.automation.steps", "framework/automation/hooks"},
        plugin = {"pretty",
                "html:target/cucumber-html-report/index.html",
                "json:target/cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        }
)
public class TestRunnerStore extends AbstractTestNGCucumberTests {

    /**
     * Scenarios run sequentially. Parallel {@code DataProvider} plus Surefire {@code parallel} caused
     * flaky Chrome usage on GitHub Actions (limited RAM / multiple browsers). Re-enable
     * {@code parallel = true} for heavy local multi-scenario runs if CI stability is not a concern.
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
