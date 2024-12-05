package pages.modal;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

@Getter
public class AddGoodModalPage {
    private final WebDriverWait webDriverWait;

    @FindBy(xpath = "//label[contains(text(), 'Наименование')]/following-sibling::input")
    private WebElement nameInput;

    @FindBy(xpath = "//label[contains(text(), 'Тип')]/following-sibling::select")
    private WebElement typeSelect;

    @FindBy(xpath = "//label[contains(text(), 'Экзотический')]/..")
    private WebElement exoticCheckbox;

    @FindBy(xpath = "//button[contains(text(), 'Сохранить')]")
    private WebElement saveButton;

    @FindBy(xpath = "//button[contains(@data-dismiss, 'modal')]")
    private WebElement modalCloseButton;

    public AddGoodModalPage(WebDriver webDriver) {
        webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        PageFactory.initElements(webDriver, this);
    }

    public void selectType(GoodType typeValue) {
        Select temp = new Select(typeSelect);
        temp.selectByValue(typeValue.toString());
    }

    public AddGoodModalPage setNameInput(String name) {
        nameInput.clear();
        nameInput.sendKeys(name);
        if (!Objects.equals(nameInput.getDomProperty("value"), name))
            throw new IllegalStateException("Значение поля 'Наименование' не было установлено");
        return this;
    }

    public AddGoodModalPage clickExoticCheckbox() {
        exoticCheckbox.findElement(By.xpath("./input")).click();
        return this;
    }

    public AddGoodModalPage clickSaveButton() {
        saveButton.click();
        return this;
    }

    public boolean nameInputIsDisplayed() {
        return webDriverWait.until(ExpectedConditions.visibilityOf(nameInput)).isDisplayed();
    }

    public void assertModalClosed() {
        try {
            webDriverWait.until(ExpectedConditions.invisibilityOfAllElements(
                    nameInput,
                    typeSelect,
                    exoticCheckbox,
                    saveButton
            ));
        } catch (WebDriverException ignored) {
        }
    }
}