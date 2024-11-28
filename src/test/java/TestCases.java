import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.GoodsPage;
import pages.StartPage;
import pages.modal.GoodType;
import pages.navbar.sandbox.SandboxItem;

import java.util.List;

import static helpers.Helpers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestCases extends BaseTest {

    @Test
    @DisplayName("Добавление нового товара типа 'Овощ'")
    void addNewGoodVegetableTypeTest() {
        StartPage startPage = new StartPage(chromeDriver);
        startPage
                .getNavigationMenu()
                .clickSandboxLink()
                .getSandboxDropdownMenu()
                .clickItemByText(SandboxItem.GOODS);

        GoodsPage goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialData = goodsPage.parseTableWithoutStalenessException();

        // Шаг 1: проверка
        assertTrue(chromeDriver.getCurrentUrl().contains("/food"));
        assertTrue(goodsPage.getGoodsTable().isDisplayed());
        assertEquals(goodsPage.getTableHeaders(), List.of("#", "Наименование", "Тип", "Экзотический"));
        assertTrue(goodsPage.getAddButton().isDisplayed() && goodsPage.getAddButton().isEnabled());
        assertEquals("rgba(0, 123, 255, 1)", goodsPage.getAddButton().getCssValue("background-color"));

        // Шаг 2
        goodsPage.clickAddButton();
        var addGoodModalPage = goodsPage.getAddGoodModalPage();

        // Шаг 2: проверка
        assertTrue(addGoodModalPage.nameInputIsDisplayed());
        assertTrue(addGoodModalPage.getTypeSelect().isDisplayed());
        assertFalse(addGoodModalPage.getExoticCheckbox().isSelected());
        assertTrue(addGoodModalPage.getSaveButton().isDisplayed() && addGoodModalPage.getSaveButton().isEnabled());
        assertEquals("rgba(0, 123, 255, 1)", addGoodModalPage.getSaveButton().getCssValue("background-color"));

        // Шаг 2
        var randomCyrillicString = getRandomCyrillicCharactersString(100);
        addGoodModalPage.setNameInput(randomCyrillicString);

        // Шаг 6-7
        var randomOtherCharactersString = getRandomOtherCharactersString(10);
        addGoodModalPage.setNameInput(randomOtherCharactersString)
                .selectType(GoodType.VEGETABLE);

        assertEquals(addGoodModalPage.getTypeSelect().getDomProperty("value"), GoodType.VEGETABLE.toString());

        // Шаг 8
        addGoodModalPage.clickSaveButton()
                .assertModalClosed();

        // Шаг проверки таблицы, ещё раз инициализируем страницу, чтобы не было StaleElementReferenceException
        goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialDataUpdated = goodsPage.parseTableWithoutStalenessException();
        assertEquals(goodsTableInitialData.size() + 1, goodsTableInitialDataUpdated.size());

        var addedRow = goodsTableInitialDataUpdated.get(goodsTableInitialDataUpdated.size() - 1);
        assertEquals(addedRow.get("Наименование"), randomOtherCharactersString);
        assertEquals(addedRow.get("Тип"), GoodType.VEGETABLE.getValue());
        assertFalse(Boolean.parseBoolean(addedRow.get("Экзотический")));

        // Повтор шагов 6-7-8 с другими значениями
        goodsPage.clickAddButton();

        var randomLatinCharactersString = getRandomLatinCharactersString(10);
        addGoodModalPage.setNameInput(randomLatinCharactersString)
                .selectType(GoodType.VEGETABLE);

        assertEquals(addGoodModalPage.getTypeSelect().getDomProperty("value"), GoodType.VEGETABLE.toString());

        addGoodModalPage.clickExoticCheckbox()
                .clickSaveButton()
                .assertModalClosed();

        // Шаг проверки таблицы, ещё раз инициализируем страницу, чтобы не было StaleElementReferenceException
        goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialDataUpdatedSecond = goodsPage.parseTableWithoutStalenessException();
        assertEquals(goodsTableInitialData.size() + 2, goodsTableInitialDataUpdatedSecond.size());

        var secondAddedRow = goodsTableInitialDataUpdatedSecond.get(goodsTableInitialDataUpdatedSecond.size() - 1);
        assertEquals(secondAddedRow.get("Наименование"), randomLatinCharactersString);
        assertEquals(secondAddedRow.get("Тип"), GoodType.VEGETABLE.getValue());
        assertTrue(Boolean.parseBoolean(secondAddedRow.get("Экзотический")));
    }

    @Test
    @DisplayName("Добавление нового товара типа 'Фрукт'")
    void addNewGoodFruitTypeTest() {
        StartPage startPage = new StartPage(chromeDriver);
        startPage
                .getNavigationMenu()
                .clickSandboxLink()
                .getSandboxDropdownMenu()
                .clickItemByText(SandboxItem.GOODS);

        GoodsPage goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialData = goodsPage.parseTableWithoutStalenessException();

        // Шаг 1: проверка
        assertTrue(chromeDriver.getCurrentUrl().contains("/food"));
        assertTrue(goodsPage.getGoodsTable().isDisplayed());
        assertEquals(goodsPage.getTableHeaders(), List.of("#", "Наименование", "Тип", "Экзотический"));
        assertTrue(goodsPage.getAddButton().isDisplayed() && goodsPage.getAddButton().isEnabled());
        assertEquals("rgba(0, 123, 255, 1)", goodsPage.getAddButton().getCssValue("background-color"));

        // Шаг 2
        goodsPage.clickAddButton();
        var addGoodModalPage = goodsPage.getAddGoodModalPage();

        // Шаг 2: проверка
        assertTrue(addGoodModalPage.nameInputIsDisplayed());
        assertTrue(addGoodModalPage.getTypeSelect().isDisplayed());
        assertFalse(addGoodModalPage.getExoticCheckbox().isSelected());
        assertTrue(addGoodModalPage.getSaveButton().isDisplayed() && addGoodModalPage.getSaveButton().isEnabled());
        assertEquals("rgba(0, 123, 255, 1)", addGoodModalPage.getSaveButton().getCssValue("background-color"));

        // Шаг 6-7
        var fruitName = "Слива";
        addGoodModalPage.setNameInput(fruitName)
                .selectType(GoodType.FRUIT);

        assertEquals(addGoodModalPage.getTypeSelect().getDomProperty("value"), GoodType.FRUIT.toString());

        // Шаг 8
        addGoodModalPage
                .clickExoticCheckbox()
                .clickSaveButton()
                .assertModalClosed();

        // Шаг проверки таблицы, ещё раз инициализируем страницу, чтобы не было StaleElementReferenceException
        goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialDataUpdated = goodsPage.parseTableWithoutStalenessException();
        assertEquals(goodsTableInitialData.size() + 1, goodsTableInitialDataUpdated.size());

        var addedRow = goodsTableInitialDataUpdated.get(goodsTableInitialDataUpdated.size() - 1);
        assertEquals(addedRow.get("Наименование"), fruitName);
        assertEquals(addedRow.get("Тип"), GoodType.FRUIT.getValue());
        assertTrue(Boolean.parseBoolean(addedRow.get("Экзотический")));

        // Сброс таблицы до исходного состояния
        goodsPage
                .getNavigationMenu()
                .clickSandboxLink()
                .getSandboxDropdownMenu()
                .clickItemByText(SandboxItem.RESET);

        goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialDataRestored = goodsPage.parseTableWithoutStalenessException();
        assertEquals(goodsTableInitialData.size(), goodsTableInitialDataRestored.size());
    }
}