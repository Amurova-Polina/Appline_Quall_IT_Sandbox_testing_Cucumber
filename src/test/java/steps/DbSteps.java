package steps;

import database.Good;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.bg.И;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static database.Good.parseResultSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DbSteps {
    String selectQuery = "SELECT * FROM FOOD";
    List<Good> listBeforeAdding;

    @И("Фиксируем исходное состояние таблицы в базе данных")
    public void dbTableHasInitialData() {
        listBeforeAdding = getGoodsInDb();
    }

    @И("Таблица в базе данных не содержит добавляемый товар с полями:")
    public void assertDbTableHasNotAddingGood(DataTable dataTable) {
        Good expectedGood = new Good(dataTable.asMaps().get(0));

        long isTableHasAddingGood = listBeforeAdding
                .stream()
                .filter(good -> good.getName().equals(expectedGood.getName()))
                .filter(good -> good.getType().equals(expectedGood.getType()))
                .filter(good -> good.isExotic() == expectedGood.isExotic())
                .count();

        assertEquals(
                0,
                isTableHasAddingGood,
                "Таблица уже содержит добавляемое значение: %s".formatted(expectedGood.toString())
        );
    }

    @И("Таблица в базе данных обновляется, в таблице появляется 1 новая строка с полями:")
    public void dbTableUpdatedWithRow(DataTable dataTable) {
        Good addedGood = new Good(dataTable.asMaps().get(0));
        assertTrue(
                getGoodsInDb().contains(addedGood),
                "Таблица в базе данных не содержит добавленное значение: %s".formatted(addedGood.toString())
        );
    }

    @И("Таблица в базе данных вернулась в исходное состояние")
    public void dbTableReturnsToInitialCondition() {
        List<Good> listAfterReset = getGoodsInDb();
        assertEquals(
                listAfterReset,
                listBeforeAdding,
                "Таблица в базе данных не соответствует исходному состоянию"
        );
    }

    private List<Good> getGoodsInDb() {
        try {
            ResultSet rs = Hooks.statement.executeQuery(selectQuery);
            return parseResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}