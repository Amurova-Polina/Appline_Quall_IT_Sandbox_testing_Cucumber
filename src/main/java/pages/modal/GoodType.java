package pages.modal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum GoodType {
    VEGETABLE("Овощ"),
    FRUIT("Фрукт");

    private final String value;

    public static GoodType getByValue(String expectedValue) {
        return Arrays
                .stream(GoodType.values())
                .filter(type -> type.getValue().equalsIgnoreCase(expectedValue))
                .findFirst()
                .get();
    }
}