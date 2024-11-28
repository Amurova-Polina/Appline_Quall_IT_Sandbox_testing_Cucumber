package pages.navbar;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.navbar.sandbox.SandboxDropdownMenu;

@Getter
public class NavigationMenu {
    private final SandboxDropdownMenu sandboxDropdownMenu;

    private final String container = "//nav";

    @FindBy(xpath = container + "//a[contains(text(), 'XSD-схемы')]")
    private WebElement xsdSchemasLink;

    @FindBy(xpath = container + "//a[contains(text(), 'Песочница')]")
    private WebElement sandboxLink;

    @FindBy(xpath = container + "//a[contains(text(), 'API')]")
    private WebElement apiLink;

    public NavigationMenu(WebDriver webDriver) {
        PageFactory.initElements(webDriver, this);
        sandboxDropdownMenu = new SandboxDropdownMenu(webDriver);
    }

    public NavigationMenu clickSandboxLink() {
        sandboxLink.click();
        return this;
    }
}