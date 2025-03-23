package framework.automation.pages;

import framework.automation.utils.WebActions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.List;

public class HomePage {

    private final WebActions webActions;

    @FindBy(how = How.NAME, using = "search")
    public WebElement inputSearch;

    @FindBy(how = How.XPATH, using = "//button[@class='btn btn-default btn-lg']")
    public WebElement btnSearch;

    @FindBy(how = How.XPATH, using = "(//button[contains(@onclick, 'cart.add')])[1]")
    public WebElement btnAddToCart;

    @FindBy(how = How.ID, using = "cart-total")
    public WebElement btnItemsCart;

    @FindBy(how = How.XPATH, using = "//strong[contains(text(), 'View Cart')]")
    public WebElement lblViewCart;

    @FindBy(how = How.XPATH, using = "//table[contains(@class, 'table-bordered')]//td[@class='text-left']/a")
    public WebElement lblItemAdded;

    @FindBy(how = How.XPATH, using = "(//button[@data-original-title='Remove'])[1]")
    public WebElement btnRemoveItem;

    @FindBy(how = How.XPATH, using = "//td[@class='text-left']/a[text()='iPhone']")
    public List<WebElement> listItem;

    @FindBy(how = How.XPATH, using = "//h1[contains(text(),'Shopping Cart')]//following-sibling::p")
    public WebElement lblCartEmpty;

    public HomePage(WebDriver driver) {
        this.webActions = new WebActions(driver);
        PageFactory.initElements(driver, this);
    }

    private int DEFAULT_TIME = 100;

    public void setInputSearch(String value) {
        webActions.sendText(inputSearch,value,DEFAULT_TIME);
    }

    public void clickOnSearchButton(){
        webActions.click(btnSearch,DEFAULT_TIME);
    }

    public void clickOnAddToCartButton(){
        webActions.click(btnAddToCart,DEFAULT_TIME);
    }

    public void clickOnItemsCartButton(){
        webActions.click(btnItemsCart,DEFAULT_TIME);
    }

    public void clickOnViewCartLabel(){
        webActions.click(lblViewCart, DEFAULT_TIME);
    }

    public String getValueTable(){
        return webActions.getText(lblItemAdded, DEFAULT_TIME);
    }

    public void clickOnRemoveItem(){
        webActions.click(btnRemoveItem, DEFAULT_TIME);
    }

    public void validateItemRemoved(){
        Assert.assertFalse(webActions.isVisible(listItem.get(0),DEFAULT_TIME));
        Assert.assertTrue(listItem.isEmpty());
    }

    public void isDisplayedLabelCartEmpty(){
        Assert.assertTrue(webActions.isVisible(lblCartEmpty, DEFAULT_TIME));
    }

}
