package framework.automation.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

/**
 * Provides common web actions to interact with web elements using Selenium.
 * Includes actions like clicking, sending text, retrieving text, and checking visibility.
 * Each instance is bound to a single {@link WebDriver} (thread-safe for parallel TestNG runs).
 */
@SuppressWarnings("null") // Selenium / Guava types lack @NonNull contracts; driver is enforced in constructor
@Slf4j
public class WebActions {

    private final WebDriver driver;

    /**
     * Constructor that initializes the WebDriver instance.
     *
     * @param driver The WebDriver instance.
     */
    public WebActions(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver");
    }

    /**
     * Satisfies null-analysis for {@link WebDriverWait} (field is already non-null after construction).
     */
    private WebDriver nonNullDriver() {
        return Objects.requireNonNull(driver, "driver");
    }

    /**
     * Waits for an element to be clickable within the given timeout.
     *
     * @param element The WebElement to wait for.
     * @param timeout The maximum time to wait in seconds.
     */
    private void waitForElement(WebElement element, int timeout) {
        try {
            new WebDriverWait(nonNullDriver(), Duration.ofSeconds(timeout))
                    .until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            log.error("Error waiting for element to be clickable: {}", element, e);
            throw e;
        }
    }

    /**
     * Waits until the element is visible (suitable for reading text; does not require clickability).
     */
    private void waitForElementVisible(WebElement element, int timeout) {
        try {
            new WebDriverWait(nonNullDriver(), Duration.ofSeconds(timeout))
                    .until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            log.error("Error waiting for element visibility: {}", element, e);
            throw e;
        }
    }

    /**
     * Clicks on a web element after waiting for it to be clickable.
     * The element is highlighted before clicking.
     *
     * @param element The WebElement to click.
     * @param timeout The maximum wait time in seconds.
     */
    public void click(WebElement element, int timeout) {
        try {
            waitForElement(element, timeout);
            highlightElement(element);
            element.click();
        } catch (Exception e) {
            log.error("Error clicking element: {}", element, e);
            throw e;
        }
    }

    /**
     * Sends text to a web element after waiting for it to be clickable.
     * Clears any existing text in the element before sending new input.
     * The element is highlighted after the action.
     *
     * @param element The WebElement to send text to.
     * @param text    The text to send.
     * @param timeout The maximum wait time in seconds.
     */
    public void sendText(WebElement element, String text, int timeout) {
        try {
            waitForElement(element, timeout);
            element.clear();
            element.sendKeys(text);
            highlightElement(element);
        } catch (Exception e) {
            log.error("Error sending text '{}' to element: {}", text, element, e);
            throw e;
        }
    }

    /**
     * Retrieves the visible text of a web element after waiting for it to be visible.
     * Uses {@link ExpectedConditions#visibilityOf} (not {@code elementToBeClickable}), since links or
     * labels in tables may be readable without satisfying the clickability condition.
     *
     * @param element The WebElement to retrieve text from.
     * @param timeout The maximum wait time in seconds.
     * @return The text content of the element.
     */
    public String getText(WebElement element, int timeout) {
        try {
            waitForElementVisible(element, timeout);
            highlightElement(element);
            return element.getText();
        } catch (Exception e) {
            log.error("Error reading text from element: {}", element, e);
            throw e;
        }
    }

    /**
     * Checks if an element is visible within the given timeout.
     * The element is highlighted if visible.
     *
     * @param element The WebElement to check visibility for.
     * @param timeout The maximum wait time in seconds.
     * @return True if the element is visible, false otherwise.
     */
    public boolean isVisible(WebElement element, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(nonNullDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOf(element));
            highlightElement(element);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Highlights a web element by adding a red border around it.
     * This is useful for debugging and visually tracking element interactions.
     *
     * @param element The WebElement to highlight.
     */
    private void highlightElement(WebElement element) {
        WebDriver d = nonNullDriver();
        if (d instanceof JavascriptExecutor) {
            ((JavascriptExecutor) d).executeScript(
                    "arguments[0].style.border='3px solid red'", element);
        }
    }

}
