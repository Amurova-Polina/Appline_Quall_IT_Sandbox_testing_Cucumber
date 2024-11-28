package pages.modal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GoodType {
    VEGETABLE("Овощ"),
    FRUIT("Фрукт");

    private final String value;
}