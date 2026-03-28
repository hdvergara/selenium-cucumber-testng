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

    /** Parallelizes scenarios; {@code super.scenarios()} delegates to {@link io.cucumber.testng.TestNGCucumberRunner#provideScenarios()}. */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
