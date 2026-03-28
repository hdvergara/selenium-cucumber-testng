package framework.automation.components;

import framework.automation.utils.ConfigLoader;
import framework.automation.utils.WebActions;
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

    @FindBy(how = How.ID, using = "cart-total")
    private WebElement btnCartTotal;

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
        webActions.click(btnCartTotal, explicitWaitSeconds);
    }

    public void clickOnViewCartLabel() {
        webActions.click(lblViewCart, explicitWaitSeconds);
    }

    public boolean isCartEmptyMessageDisplayed() {
        return webActions.isVisible(lblCartEmpty, explicitWaitSeconds);
    }
}
