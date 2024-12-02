import database.Good;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.GoodsPage;
import pages.StartPage;
import pages.modal.GoodType;
import pages.navbar.sandbox.SandboxItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static database.Good.parseResultSet;
import static helpers.Helpers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestCases extends BaseTest {
    private final String selectQuery = "SELECT * FROM FOOD";

    @Test
    @DisplayName("Добавление нового товара типа 'Овощ'")
    void addNewGoodVegetableTypeTest() {
        // ui
        StartPage startPage = new StartPage(chromeDriver);
        startPage
                .getNavigationMenu()
                .clickSandboxLink()
                .getSandboxDropdownMenu()
                .clickItemByText(SandboxItem.GOODS);

        GoodsPage goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialData = goodsPage.parseTableWithoutStalenessException();

        // Шаг 1.2: проверка ui
        assertTrue(chromeDriver.getCurrentUrl().contains("/food"));
        assertTrue(goodsPage.getGoodsTable().isDisplayed());
        assertEquals(goodsPage.getTableHeaders(), List.of("#", "Наименование", "Тип", "Экзотический"));
        assertTrue(goodsPage.getAddButton().isDisplayed() && goodsPage.getAddButton().isEnabled());
        assertEquals("rgba(0, 123, 255, 1)", goodsPage.getAddButton().getCssValue("background-color"));

        // Шаг 1.3: фиксируем исходное состояние таблицы в БД
        List<Good> listBeforeAdding;
        try {
            ResultSet rs = statement.executeQuery(selectQuery);
            listBeforeAdding = parseResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Шаг 1.4: проверка UI модального окна добавления товара
        goodsPage.clickAddButton();
        var addGoodModalPage = goodsPage.getAddGoodModalPage();

        assertTrue(addGoodModalPage.nameInputIsDisplayed());
        assertTrue(addGoodModalPage.getTypeSelect().isDisplayed());
        assertFalse(addGoodModalPage.getExoticCheckbox().isSelected());
        assertTrue(addGoodModalPage.getSaveButton().isDisplayed() && addGoodModalPage.getSaveButton().isEnabled());
        assertEquals("rgba(0, 123, 255, 1)", addGoodModalPage.getSaveButton().getCssValue("background-color"));

        // Шаг 2-3: проверка того, что поле принимает значение большой длины
        var randomCyrillicString = getRandomCyrillicCharactersString(100);
        addGoodModalPage.setNameInput(randomCyrillicString)
                .clickModalCloseButton();

        //Шаг 4: проверка того, что в БД нет товара, который собираемся добавить
        var randomOtherCharactersString = getRandomOtherCharactersString(10);

        long isTableContainsAddingName = listBeforeAdding
                .stream()
                .filter(good -> good.getName().contains(randomOtherCharactersString))
                .count();
        assertEquals(isTableContainsAddingName,
                0,
                "Таблица содержит добавляемое наименование: '%s'".formatted(randomOtherCharactersString));

        // Шаг 5-7: ввести значение со спецсимволами в поле "Наименование", выбрать тип "овощ"
        goodsPage.clickAddButton();
        addGoodModalPage.setNameInput(randomOtherCharactersString)
                .selectType(GoodType.VEGETABLE);

        assertEquals(addGoodModalPage.getTypeSelect().getDomProperty("value"), GoodType.VEGETABLE.toString());

        // Шаг 8: нажать кнопку "сохранить"
        addGoodModalPage.clickSaveButton()
                .assertModalClosed();

        // Шаг проверки UI таблицы (ещё раз инициализируем страницу, чтобы не было StaleElementReferenceException)
        goodsPage = new GoodsPage(chromeDriver);

        var goodsTableInitialDataUpdated = goodsPage.parseTableWithoutStalenessException();
        assertEquals(goodsTableInitialData.size() + 1, goodsTableInitialDataUpdated.size());

        var addedRow = goodsTableInitialDataUpdated.getLast();
        assertEquals(addedRow.get("Наименование"), randomOtherCharactersString);
        assertEquals(addedRow.get("Тип"), GoodType.VEGETABLE.getValue());
        assertFalse(Boolean.parseBoolean(addedRow.get("Экзотический")));

        // Шаг 10: проверка записи в БД
        List<Good> listAfterAddingRandomOtherCharactersString;
        try {
            ResultSet rs1 = statement.executeQuery(selectQuery);
            listAfterAddingRandomOtherCharactersString = parseResultSet(rs1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Good addedNotExoticGood = new Good(randomOtherCharactersString, GoodType.VEGETABLE.toString(), false);
        assertTrue(listAfterAddingRandomOtherCharactersString.contains(addedNotExoticGood));

        // Шаг 10-14: повтор шагов 4-8 с другими значениями
        var randomLatinCharactersString = getRandomLatinCharactersString(10);

        goodsPage.clickAddButton();
        addGoodModalPage
                .setNameInput(randomLatinCharactersString)
                .selectType(GoodType.VEGETABLE);

        assertEquals(addGoodModalPage.getTypeSelect().getDomProperty("value"), GoodType.VEGETABLE.toString());

        addGoodModalPage.clickExoticCheckbox()
                .clickSaveButton()
                .assertModalClosed();

        // Шаг проверки таблицы, ещё раз инициализируем страницу, чтобы не было StaleElementReferenceException
        goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialDataUpdatedSecond = goodsPage.parseTableWithoutStalenessException();
        assertEquals(goodsTableInitialData.size() + 2, goodsTableInitialDataUpdatedSecond.size());

        var secondAddedRow = goodsTableInitialDataUpdatedSecond.getLast();
        assertEquals(secondAddedRow.get("Наименование"), randomLatinCharactersString);
        assertEquals(secondAddedRow.get("Тип"), GoodType.VEGETABLE.getValue());
        assertTrue(Boolean.parseBoolean(secondAddedRow.get("Экзотический")));

        // Шаг 15: проверка таблицы в БД
        List<Good> listAfterAddingRandomLatinCharactersString;
        try {
            ResultSet rs2 = statement.executeQuery(selectQuery);
            listAfterAddingRandomLatinCharactersString = parseResultSet(rs2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Good addedExoticGood = new Good(randomLatinCharactersString, GoodType.VEGETABLE.toString(), true);
        assertTrue(listAfterAddingRandomLatinCharactersString.contains(addedExoticGood));

        // Шаг 16-17: возвращение таблицы БД в исходное состояние
        goodsPage
                .getNavigationMenu()
                .clickSandboxLink()
                .getSandboxDropdownMenu()
                .clickItemByText(SandboxItem.RESET);

        goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialDataRestored = goodsPage.parseTableWithoutStalenessException();
        assertEquals(goodsTableInitialData.size(), goodsTableInitialDataRestored.size());

        // Проверка успешности операции
        List<Good> listAfterAddingAllItems;
        try {
            ResultSet rs3 = statement.executeQuery(selectQuery);
            listAfterAddingAllItems = parseResultSet(rs3);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertEquals(listBeforeAdding.size(), listAfterAddingAllItems.size());
        assertFalse(listAfterAddingAllItems.contains(addedNotExoticGood));
        assertFalse(listAfterAddingAllItems.contains(addedExoticGood));
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

        var fruitName = "Манго";
        var secondfruitName = "Слива";

        // Шаг 1.2: проверка UI
        assertTrue(chromeDriver.getCurrentUrl().contains("/food"));
        assertTrue(goodsPage.getGoodsTable().isDisplayed());
        assertEquals(goodsPage.getTableHeaders(), List.of("#", "Наименование", "Тип", "Экзотический"));
        assertTrue(goodsPage.getAddButton().isDisplayed() && goodsPage.getAddButton().isEnabled());
        assertEquals("rgba(0, 123, 255, 1)", goodsPage.getAddButton().getCssValue("background-color"));

        // Шаг 1.3: фиксируем исходное состояние таблицы в БД
        List<Good> listBeforeAdding;
        try {
            ResultSet rs = statement.executeQuery(selectQuery);
            listBeforeAdding = parseResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Шаг 1.4: проверка UI модального окна добавления товара
        goodsPage.clickAddButton();
        var addGoodModalPage = goodsPage.getAddGoodModalPage();

        assertTrue(addGoodModalPage.nameInputIsDisplayed());
        assertTrue(addGoodModalPage.getTypeSelect().isDisplayed());
        assertFalse(addGoodModalPage.getExoticCheckbox().isSelected());
        assertTrue(addGoodModalPage.getSaveButton().isDisplayed() && addGoodModalPage.getSaveButton().isEnabled());
        assertEquals("rgba(0, 123, 255, 1)", addGoodModalPage.getSaveButton().getCssValue("background-color"));

        //Шаг 2: проверка того, что в БД нет товара, который собираемся добавить

        long isTableContainsAddingName = listBeforeAdding
                .stream()
                .filter(good -> good.getName().contains(fruitName))
                .count();
        assertEquals(isTableContainsAddingName,
                0,
                "Таблица содержит добавляемое наименование: '%s'".formatted(fruitName));


        //Шаг 3: добавить в бд дубликаты товаров, которые будем добавлять потом
        Good addingGood = new Good(fruitName, "FRUIT", true);

        try {
            String insert = String.format(
                    "INSERT INTO FOOD (FOOD_NAME, FOOD_TYPE, FOOD_EXOTIC) VALUES ('%s', '%s', %b)",
                    addingGood.getName(),
                    addingGood.getType(),
                    addingGood.isExotic()
            );
            int affectedRows = statement.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Шаг 4-8
        addGoodModalPage.setNameInput(fruitName)
                .selectType(GoodType.FRUIT);

        assertEquals(addGoodModalPage.getTypeSelect().getDomProperty("value"), GoodType.FRUIT.toString());

        addGoodModalPage
                .clickExoticCheckbox()
                .clickSaveButton()
                .assertModalClosed();

        // Шаг проверки таблицы, ещё раз инициализируем страницу, чтобы не было StaleElementReferenceException
        goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialDataUpdated = goodsPage.parseTableWithoutStalenessException();
        assertEquals(goodsTableInitialData.size() + 1, goodsTableInitialDataUpdated.size());

        var addedRow = goodsTableInitialDataUpdated.getLast();
        assertEquals(addedRow.get("Наименование"), fruitName);
        assertEquals(addedRow.get("Тип"), GoodType.FRUIT.getValue());
        assertTrue(Boolean.parseBoolean(addedRow.get("Экзотический")));

        // Шаг 9: Проверить, что дубликат сохранился в БД
        List<Good> listAfterAddingFruitName;
        try {
            ResultSet rs2 = statement.executeQuery(selectQuery);
            listAfterAddingFruitName = parseResultSet(rs2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Good addedFruitName = new Good(fruitName, GoodType.FRUIT.toString(), true);
        assertTrue(listAfterAddingFruitName.contains(addedFruitName));


        // Шаг 10-14: Повторение шагов 4-8 с другими тестовыми данными
        goodsPage.clickAddButton();
        addGoodModalPage.setNameInput(secondfruitName)
                .selectType(GoodType.FRUIT);

        assertEquals(addGoodModalPage.getTypeSelect().getDomProperty("value"), GoodType.FRUIT.toString());

        addGoodModalPage
                .clickSaveButton()
                .assertModalClosed();


        // Шаг проверки таблицы, ещё раз инициализируем страницу, чтобы не было StaleElementReferenceException
        goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialDataUpdated1 = goodsPage.parseTableWithoutStalenessException();
        assertEquals(goodsTableInitialData.size() + 2, goodsTableInitialDataUpdated1.size());

        var addedRow1 = goodsTableInitialDataUpdated1.getLast();
        assertEquals(addedRow1.get("Наименование"), secondfruitName);
        assertEquals(addedRow1.get("Тип"), GoodType.FRUIT.getValue());
        assertFalse(Boolean.parseBoolean(addedRow1.get("Экзотический")));


        // Шаг проверки записи в БД
        List<Good> listAfterAddingSecondFruitName;
        try {
            ResultSet rs3 = statement.executeQuery(selectQuery);
            listAfterAddingSecondFruitName = parseResultSet(rs3);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Good addedSecondFruitName = new Good(secondfruitName, GoodType.FRUIT.toString(),false);
        assertTrue(listAfterAddingSecondFruitName.contains(addedSecondFruitName));


        // Шаг 16-17: сброс таблицы БД до исходного состояния, проверка успешности операции
        goodsPage
                .getNavigationMenu()
                .clickSandboxLink()
                .getSandboxDropdownMenu()
                .clickItemByText(SandboxItem.RESET);

        goodsPage = new GoodsPage(chromeDriver);
        var goodsTableInitialDataRestored = goodsPage.parseTableWithoutStalenessException();
        assertEquals(goodsTableInitialData.size(), goodsTableInitialDataRestored.size());

        List<Good> listAfterReset;
        try {
            ResultSet rs4 = statement.executeQuery(selectQuery);
            listAfterReset = parseResultSet(rs4);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertEquals(listBeforeAdding.size(), listAfterReset.size());
        assertFalse(listAfterReset.contains(addedFruitName));
        assertFalse(listAfterReset.contains(addedSecondFruitName));
    }
}