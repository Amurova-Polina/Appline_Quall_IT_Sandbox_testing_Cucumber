package database;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Good {
    private String name;
    private String type;
    private boolean isExotic;

    public static List<Good> parseResultSet(ResultSet rs) throws SQLException {
        List<Good> goodList = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("FOOD_NAME");
            String type = rs.getString("FOOD_TYPE");
            boolean isExotic = rs.getBoolean("FOOD_EXOTIC");
            Good good = new Good(name, type, isExotic);
            goodList.add(good);
        }
        return goodList;
    }
}