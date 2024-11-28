package pages.navbar.sandbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SandboxItem {
    GOODS("Товары"),
    RESET("Сброс данных");

    private final String value;
}