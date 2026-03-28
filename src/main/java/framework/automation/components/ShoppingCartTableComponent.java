package framework.automation.components;

import framework.automation.utils.ConfigLoader;
import framework.automation.utils.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

/**
 * Shopping cart product table (Shopping Cart page): product links by name, per-row Remove button
 * (dynamic locators), and checks that a line item is no longer displayed.
 */
public class ShoppingCartTableComponent {

    /** First product name cell in the cart table (same role as the legacy {@code lblItemAdded}). */
    private static final By FIRST_CART_PRODUCT_LINK =
            By.xpath("//table[contains(@class,'table-bordered')]//td[@class='text-left']/a");

    /**
     * Remove button on the row for the given product (not a single static {@code @FindBy}: one row per product).
     */
    private By removeButtonForProduct(String productName) {
        return By.xpath("//tr[.//td[@class='text-left']/a[text()=" + xpathLiteral(productName)
                + "]]//button[@data-original-title='Remove']");
    }

    /**
     * Locator for the product link in the cart table (exact link text).
     */
    private By getProductLocator(String productName) {
        return By.xpath("//td[@class='text-left']/a[text()=" + xpathLiteral(productName) + "]");
    }

    /**
     * Safe XPath string literal when the text contains single quotes (uses {@code concat}).
     */
    private static String xpathLiteral(String value) {
        if (value == null) {
            return "''";
        }
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        String[] parts = value.split("'", -1);
        StringBuilder b = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                b.append(", \"'\", ");
            }
            b.append("'").append(parts[i]).append("'");
        }
        b.append(")");
        return b.toString();
    }

    private final WebDriver driver;
    private final WebActions webActions;
    private final int explicitWaitSeconds;

    public ShoppingCartTableComponent(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver");
        this.webActions = new WebActions(driver);
        this.explicitWaitSeconds = ConfigLoader.getExplicitWait();
    }

    /**
     * Visible text of the product on the first cart line (single-item scenario).
     * The step compares {@code productName} against the text read here.
     */
    public String getCartProductNameText(String productName) {
        Objects.requireNonNull(productName, "productName");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitSeconds));
        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST_CART_PRODUCT_LINK));
        return link.getText().trim();
    }

    public void clickOnRemoveItem(String productName) {
        WebElement remove = driver.findElement(removeButtonForProduct(productName));
        webActions.click(remove, explicitWaitSeconds);
    }

    /**
     * @return {@code true} when the product link is no longer present or not visible
     *         (explicit wait; {@code findElements().isEmpty()} alone is not enough if the DOM keeps hidden nodes).
     */
    public boolean validateItemRemoved(String productName) {
        By locator = getProductLocator(productName);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(explicitWaitSeconds))
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return driver.findElements(locator).isEmpty();
        }
    }
}
