package framework.automation.steps;

import framework.automation.components.SearchComponent;
import framework.automation.hooks.TestContext;
import framework.automation.manager.DriverManager;
import framework.automation.pages.HomePage;
import framework.automation.utils.ConfigLoader;
import framework.automation.utils.ScreenshotUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.Objects;

public class StoreStepDefinition {

    private final WebDriver driver;
    private final HomePage homePage;
    private final TestContext testContext;

    public StoreStepDefinition(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverManager.getDriver();
        this.homePage = new HomePage(driver);
    }

    @Given("the user is on the store page")
    public void theUserIsOnTheStorePage() {
        String baseUrl = Objects.requireNonNull(ConfigLoader.getBaseUrl(), "base.url");
        driver.get(baseUrl);
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Store page loaded");
    }

    @When("the user searches for {string}")
    public void theUserSearchesFor(String productName) {
        SearchComponent search = homePage.getSearchComponent();
        search.setInputSearch(productName);
        search.clickOnSearchButton();
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Search executed for: " + productName);
    }

    @And("the user adds the first search result to the cart")
    public void theUserAddsTheFirstSearchResultToTheCart() {
        homePage.clickOnAddToCartButton();
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Added first search result to cart");
    }

    @And("the user opens the shopping cart menu")
    public void theUserOpensTheShoppingCartMenu() {
        homePage.clickOnItemsCartButton();
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Opened shopping cart menu");
    }

    @And("the user opens the shopping cart page")
    public void theUserOpensTheShoppingCartPage() {
        homePage.clickOnViewCartLabel();
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Shopping cart page opened");
    }

    @Then("the shopping cart should contain product {string}")
    public void theShoppingCartShouldContainProduct(String productName) {
        Assert.assertEquals(
                homePage.getCartProductNameText(productName).toLowerCase(),
                productName.toLowerCase(),
                "Cart product name must match the expected product.");
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Validated product in cart: " + productName);
    }

    @When("the user removes {string} from the shopping cart")
    public void theUserRemovesFromTheShoppingCart(String productName) {
        homePage.clickOnRemoveItem(productName);
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Removed from cart: " + productName);
    }

    @Then("product {string} should no longer appear in the shopping cart")
    public void productShouldNoLongerAppearInTheShoppingCart(String productName) {
        Assert.assertTrue(
                homePage.validateItemRemoved(productName),
                "Expected the cart table to contain no rows for this product (no matching links).");
        Assert.assertTrue(
                homePage.isDisplayedLabelCartEmpty(),
                "Expected the empty shopping cart message to be visible on the page.");
        ScreenshotUtil.captureScreenshot(driver, testContext.getScenario(), "Validated cart empty after removing: " + productName);
    }
}
