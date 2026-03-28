package framework.automation.components;

import framework.automation.utils.ConfigLoader;
import framework.automation.utils.WebActions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class SearchComponent {

    @FindBy(how = How.NAME, using = "search")
    private WebElement inputSearch;

    @FindBy(how = How.XPATH, using = "//button[@class='btn btn-default btn-lg']")
    private WebElement btnSearch;

    private final WebActions webActions;
    private final int explicitWaitSeconds;

    public SearchComponent(WebDriver driver) {
        this.webActions = new WebActions(driver);
        this.explicitWaitSeconds = ConfigLoader.getExplicitWait();
        PageFactory.initElements(driver, this);
    }

    public void setInputSearch(String name) {
        webActions.sendText(inputSearch, name, explicitWaitSeconds);
    }

    public void clickOnSearchButton() {
        webActions.click(btnSearch, explicitWaitSeconds);
    }
}
