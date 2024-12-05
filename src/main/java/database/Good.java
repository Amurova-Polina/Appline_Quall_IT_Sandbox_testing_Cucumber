package database;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pages.modal.GoodType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Good {
    private String name;
    private GoodType type;
    private boolean isExotic;

    public Good(Map<String, String> dataTableRow) {
        this.name = dataTableRow.get("Наименование");
        this.type = GoodType.getByValue(dataTableRow.get("Тип"));
        this.isExotic = Boolean.parseBoolean(dataTableRow.get("Экзотический"));
    }

    public static List<Good> parseResultSet(ResultSet rs) throws SQLException {
        List<Good> goodList = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("FOOD_NAME");
            String type = rs.getString("FOOD_TYPE");
            boolean isExotic = rs.getBoolean("FOOD_EXOTIC");
            Good good = new Good(name, GoodType.valueOf(type), isExotic);
            goodList.add(good);
        }
        return goodList;
    }

    @Override
    public String toString() {
        String isExoticString = isExotic ? "Экзотический" : "Не экзотический";
        return isExoticString + " товар с названием '" + name + "' и типом '" + type + "'";
    }

}