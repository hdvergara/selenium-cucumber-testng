package framework.automation.components;

import framework.automation.utils.ConfigLoader;
import framework.automation.utils.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

/**
 * Header mini-cart widget: cart total button, "View Cart" link, and empty-cart message on the
 * shopping cart page when applicable.
 */
public class MiniCartComponent {

    /** Header cart control; DOM may be replaced after add-to-cart — use {@link WebActions#click(By, int)} not a cached {@link WebElement}. */
    private static final By CART_TOTAL = By.id("cart-total");

    @FindBy(how = How.XPATH, using = "//strong[contains(text(), 'View Cart')]")
    private WebElement lblViewCart;

    @FindBy(how = How.XPATH, using = "//h1[contains(text(),'Shopping Cart')]//following-sibling::p")
    private WebElement lblCartEmpty;

    private final WebActions webActions;
    private final int explicitWaitSeconds;

    public MiniCartComponent(WebDriver driver) {
        this.webActions = new WebActions(driver);
        this.explicitWaitSeconds = ConfigLoader.getExplicitWait();
        PageFactory.initElements(driver, this);
    }

    public void clickOnCartButton() {
        webActions.click(CART_TOTAL, explicitWaitSeconds);
    }

    public void clickOnViewCartLabel() {
        webActions.click(lblViewCart, explicitWaitSeconds);
    }

    public boolean isCartEmptyMessageDisplayed() {
        return webActions.isVisible(lblCartEmpty, explicitWaitSeconds);
    }
}
