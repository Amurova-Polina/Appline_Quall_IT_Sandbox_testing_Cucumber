package pages.navbar.sandbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SandboxItem {
    GOODS("Товары"),
    RESET("Сброс данных");

    private final String value;

    public static SandboxItem getByValue(String expectedValue) {
        return Arrays
                .stream(SandboxItem.values())
                .filter(item -> item.getValue().equalsIgnoreCase(expectedValue))
                .findFirst()
                .get();
    }
}