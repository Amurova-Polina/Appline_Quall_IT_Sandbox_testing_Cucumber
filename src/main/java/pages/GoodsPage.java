package pages;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.modal.AddGoodModalPage;
import pages.navbar.NavigationMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsPage {

    @Getter
    private final NavigationMenu navigationMenu;

    @Getter
    private final AddGoodModalPage addGoodModalPage;

    private final By rowsLocator = By.xpath(".//tbody/tr");
    private final By columnsLocator = By.xpath(".//*[self::td or self::th[@scope='row']]");
    private final By headersLocator = By.xpath(".//thead//th");

    @Getter
    @FindBy(xpath = "//h5/following-sibling::table")
    private WebElement goodsTable;

    @Getter
    @FindBy(xpath = "//button[contains(text(), 'Добавить')]")
    private WebElement addButton;

    @Getter
    private List<String> tableHeaders;

    public GoodsPage(WebDriver webDriver) {
        navigationMenu = new NavigationMenu(webDriver);
        addGoodModalPage = new AddGoodModalPage(webDriver);
        PageFactory.initElements(webDriver, this);
    }

    public List<Map<String, String>> parseTableWithoutStalenessException() {
        try {
            return parseTable();
        } catch (StaleElementReferenceException e) {
            return parseTable();
        }
    }

    private List<Map<String, String>> parseTable() {
        var tableData = new ArrayList<Map<String, String>>();
        var rows = goodsTable.findElements(rowsLocator);
        this.tableHeaders = goodsTable.findElements(headersLocator)
                .stream()
                .map(header -> header.getText().trim())
                .toList();
        for (WebElement row : rows) {
            Map<String, String> rowData = new HashMap<>();
            List<WebElement> columns = row.findElements(columnsLocator);
            for (int i = 0; i < columns.size(); i++) {
                String key = tableHeaders.get(i);
                String value = columns.get(i).getText().trim();
                rowData.put(key, value);
            }
            tableData.add(rowData);
        }
        return tableData;
    }

    public void clickAddButton() {
        addButton.click();
    }
}