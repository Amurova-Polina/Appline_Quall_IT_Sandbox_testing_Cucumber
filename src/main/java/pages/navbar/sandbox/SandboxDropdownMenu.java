package pages.navbar.sandbox;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SandboxDropdownMenu {

    @FindBy(css = "div.dropdown-menu")
    private WebElement dropdownMenuContainer;

    public SandboxDropdownMenu(WebDriver webDriver) {
        PageFactory.initElements(webDriver, this);
    }

    public SandboxDropdownMenu clickItemByText(SandboxItem item) {
        dropdownMenuContainer
                .findElement(By.xpath(".//a[contains(text(), '%s')]".formatted(item.getValue())))
                .click();
        return this;
    }
}
