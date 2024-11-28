package pages;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.navbar.NavigationMenu;

@Getter
public class StartPage {
    private final NavigationMenu navigationMenu;

    public StartPage(WebDriver webDriver) {
        navigationMenu = new NavigationMenu(webDriver);
        PageFactory.initElements(webDriver, this);
    }
}