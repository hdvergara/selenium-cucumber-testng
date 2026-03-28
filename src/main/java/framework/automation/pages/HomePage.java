package framework.automation.pages;

import framework.automation.components.MiniCartComponent;
import framework.automation.components.SearchComponent;
import framework.automation.components.ShoppingCartTableComponent;
import framework.automation.utils.ConfigLoader;
import framework.automation.utils.WebActions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

    private final WebActions webActions;
    private final SearchComponent searchComponent;
    private final MiniCartComponent miniCartComponent;
    private final ShoppingCartTableComponent shoppingCartTableComponent;
    private final int explicitWaitSeconds;

    @FindBy(how = How.XPATH, using = "(//button[contains(@onclick, 'cart.add')])[1]")
    public WebElement btnAddToCart;

    public HomePage(WebDriver driver) {
        this.webActions = new WebActions(driver);
        this.searchComponent = new SearchComponent(driver);
        this.miniCartComponent = new MiniCartComponent(driver);
        this.shoppingCartTableComponent = new ShoppingCartTableComponent(driver);
        this.explicitWaitSeconds = ConfigLoader.getExplicitWait();
        PageFactory.initElements(driver, this);
    }

    /**
     * Exposes the search UI component for step definitions that interact with search directly.
     */
    public SearchComponent getSearchComponent() {
        return searchComponent;
    }

    public void setInputSearch(String value) {
        searchComponent.setInputSearch(value);
    }

    public void clickOnSearchButton() {
        searchComponent.clickOnSearchButton();
    }

    public void clickOnAddToCartButton() {
        webActions.click(btnAddToCart, explicitWaitSeconds);
    }

    public void clickOnItemsCartButton() {
        miniCartComponent.clickOnCartButton();
    }

    public void clickOnViewCartLabel() {
        miniCartComponent.clickOnViewCartLabel();
    }

    public String getCartProductNameText(String productName) {
        return shoppingCartTableComponent.getCartProductNameText(productName);
    }

    public void clickOnRemoveItem(String productName) {
        shoppingCartTableComponent.clickOnRemoveItem(productName);
    }

    public boolean validateItemRemoved(String productName) {
        return shoppingCartTableComponent.validateItemRemoved(productName);
    }

    public boolean isDisplayedLabelCartEmpty() {
        return miniCartComponent.isCartEmptyMessageDisplayed();
    }

}
