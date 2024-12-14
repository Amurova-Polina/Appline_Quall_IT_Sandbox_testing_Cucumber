package steps;

import database.Good;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.И;
import pages.GoodsPage;
import pages.StartPage;
import pages.modal.AddGoodModalPage;
import pages.modal.GoodType;
import pages.navbar.sandbox.SandboxItem;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UiSteps {
    private GoodsPage goodsPage;
    private AddGoodModalPage addGoodModalPage;
    private List<Map<String, String>> goodsTableInitialData;

    @И("Открыта страница по URL: {string}")
    public void getURL(String url) {
        Hooks.webDriver.get(url);
    }

    @И("Пользователь находится на странице \"Товары\"")
    public void userIsOnGoodsPage() {
        goodsPage = new GoodsPage(Hooks.webDriver);
    }

    @И("Пользователь нажимает на кнопку {string} в меню \"Песочница\"")
    public void getGoodsTablePage(String name) {
        SandboxItem item = SandboxItem.getByValue(name);
        StartPage startPage = new StartPage(Hooks.webDriver);
        startPage
                .getNavigationMenu()
                .clickSandboxLink()
                .getSandboxDropdownMenu()
                .clickItemByText(item);
    }

    @И("Фиксируем исходное состояние UI-таблицы")
    public void uiTableHasInitialData() {
        goodsTableInitialData = goodsPage.parseTableWithoutStalenessException();
    }

    @И("URL страницы содержит: {string}")
    public void pageUrlContainsPath(String path) {
        assertTrue(
                Hooks.webDriver.getCurrentUrl().contains(path),
                "URL страницы не содержит путь: %s".formatted(path)
        );
    }

    @И("Таблица товаров отображается")
    public void uiTableIsDisplayed() {
        assertTrue(
                goodsPage.getGoodsTable().isDisplayed(),
                "Таблица товаров не отображается"
        );
    }

    @И("Заголовки UI-таблицы: {string}, {string}, {string}, {string}")
    public void assertUiTableHeaders(String col1, String col2, String col3, String col4) {
        assertEquals(
                goodsPage.getTableHeaders(),
                List.of(col1, col2, col3, col4),
                "Заголовки UI-таблицы не соответствуют: %s, %s, %s, %s".formatted(col1, col2, col3, col4)
        );
    }

    @И("Кнопка \"Добавить\" отображается и доступна")
    public void addButtonIsDisplayedAndEnabled() {
        assertTrue(
                goodsPage.getAddButton().isDisplayed()
                        && goodsPage.getAddButton().isEnabled(),
                "Кнопка \"Добавить\" не отображается или не доступна"
        );
    }

    @И("Пользователь нажимает кнопку \"Добавить\"")
    public void userClicksAddButton() {
        goodsPage.clickAddButton();
    }

    @И("Отображается модальное окно \"Добавление товара\"")
    public void addGoodModalIsDisplayed() {
        addGoodModalPage = goodsPage.getAddGoodModalPage();
        assertTrue(
                addGoodModalPage.nameInputIsDisplayed(),
                "Модальное окно \"Добавления товара\" не отображается"
        );
    }

    @И("Поле ввода \"Наименование\" отображается")
    public void nameInputFieldIsDisplayed() {
        assertTrue(
                addGoodModalPage.nameInputIsDisplayed(),
                "Поле ввода \"Наименование\" не отображается"
        );
    }

    @И("Выпадающий список \"Тип\" отображается")
    public void typeSelectDropdownIsDisplayed() {
        assertTrue(
                addGoodModalPage.getTypeSelect().isDisplayed(),
                "Выпадающий список \"Тип\" не отображается"
        );
    }

    @И("Чекбокс \"Экзотический\" по умолчанию в неактивном состоянии")
    public void exoticCheckboxIsNotSelected() {
        assertFalse(
                addGoodModalPage.getExoticCheckbox().isSelected(),
                "Чекбокс \"Экзотический\" по умолчанию в активном состоянии"
        );
    }

    @И("Кнопка \"Сохранить\" отображается и доступна")
    public void saveButtonIsDisplayedAndEnabled() {
        assertTrue(
                addGoodModalPage.getSaveButton().isDisplayed()
                        && addGoodModalPage.getSaveButton().isEnabled(),
                "Кнопка \"Сохранить\" не отображается или не доступна"
        );
    }

    @И("Пользователь вводит {string} в поле \"Наименование\"")
    public void userEntersName(String name) {
        addGoodModalPage.setNameInput(name);
    }

    @И("Пользователь выбирает тип {string} в выпадающем списке")
    public void userSelectsType(String value) {
        var type = GoodType.getByValue(value);
        addGoodModalPage.selectType(type);
        assertEquals(
                type.toString(),
                addGoodModalPage.getTypeSelect().getDomProperty("value"),
                "Выпадающий список не заполнился значением: %s".formatted(value)
        );
    }

    @И("Пользователь устанавливает чекбокс \"Экзотический\" на значении {string}")
    public void userClicksExoticCheckbox(String active) {
        if (Boolean.parseBoolean(active))
            addGoodModalPage.clickExoticCheckbox();
        else exoticCheckboxIsNotSelected();
    }

    @И("Пользователь нажимает кнопку \"Сохранить\" и модальное окно  \"Добавление товара\" закрывается")
    public void userClicksSaveButton() {
        addGoodModalPage.clickSaveButton().assertModalClosed();
    }

    @И("UI-таблица товаров обновляется, в таблице добавлена 1 новая строка с полями:")
    public void uiTableUpdatedWithRow(DataTable dataTable) {
        var expectedGood = new Good(dataTable.asMaps().get(0));

        var goodsTableUpdated = goodsPage.parseTableWithoutStalenessException();
        var addedGood = new Good(goodsTableUpdated.get(goodsTableUpdated.size() - 1));
        assertEquals(
                expectedGood,
                addedGood,
                "Отображаемый товар и добавленный товар не соответствуют"
        );
    }

    @И("UI-таблица товаров вернулась в исходное состояние")
    public void uiTableContainsInitialData() {
        var restoredData = goodsPage.parseTableWithoutStalenessException();
        assertEquals(
                goodsTableInitialData.size(),
                restoredData.size(),
                "UI-таблица товаров не соответствует исходному состоянию"
        );
    }
}