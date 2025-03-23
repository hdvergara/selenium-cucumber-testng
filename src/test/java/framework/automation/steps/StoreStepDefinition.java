package framework.automation.steps;


import framework.automation.hooks.TestContext;
import framework.automation.manager.DriverManager;
import framework.automation.pages.HomePage;
import framework.automation.utils.ScreenshotUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class StoreStepDefinition {

    private final WebDriver driver;
    private final HomePage homePage;
    private final TestContext testContext;

    public StoreStepDefinition(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverManager.getDriver();
        this.homePage = new HomePage(driver);
    }

    @Given("The user is on the homepage")
    public void el_usuario_se_encuentra_en_la_página_principal() {
        DriverManager.getDriver().get("https://opencart.abstracta.us/");
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Home Page Loaded");
    }

    @When("They enter {string} in the search bar and press search")
    public void ingresaEnLaBarraDeBúsquedaYPresionaBuscar(String value) {
        homePage.setInputSearch(value);
        homePage.clickOnSearchButton();
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Search Executed for: " + value);
    }

    @And("They add the first search result to the cart")
    public void seleccionaLaPrimeraOpciónQueApareceEnLosResultados() {
        homePage.clickOnAddToCartButton();
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Added First Search Result to Cart");
    }

    @And("The user click on the shopping cart button")
    public void haceClicEnElBotónDelCarritoDeCompras() {
        homePage.clickOnItemsCartButton();
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Clicked on Cart Button");
    }

    @And("The press View Cart")
    public void presiona() {
        homePage.clickOnViewCartLabel();
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Viewed Cart");
    }

    @Then("It is validated that the iPhone is in the shopping cart")
    public void seValidaQueElIPhoneSeEncuentreEnElCarritoDeCompras() {
        Assert.assertEquals(homePage.getValueTable().toLowerCase(), "iphone".toLowerCase());
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Validated iPhone in Cart");
    }

    @When("The user remove the iPhone from the shopping cart")
    public void remueveElIPhoneDelCarritoDeCompras() {
        homePage.clickOnRemoveItem();
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Removed iPhone from Cart");
    }

    @Then("It is validated that the iPhone is no longer in the shopping cart")
    public void seValidaQueElIPhoneYaNoSeEncuentreEnElCarritoDeCompras() {
        homePage.isDisplayedLabelCartEmpty();
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Validated Cart is Empty");
    }
}
