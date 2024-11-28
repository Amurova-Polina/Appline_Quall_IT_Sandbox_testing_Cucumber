package helpers;

import java.util.Random;

public class Helpers {
    private static final String LATIN_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String CYRILLIC_CHARACTERS = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя";
    private static final String OTHER_CHARACTERS = "0123456789!@#$%^&*()_-+=<>?/.,:;'\"[]{}|~`\";";

    private static final Random RANDOM = new Random();

    private Helpers() {
        throw new IllegalStateException("Utility class");
    }

    public static String getRandomLatinCharactersString(int length) {
        return getRandomString(LATIN_CHARACTERS, length);
    }

    public static String getRandomCyrillicCharactersString(int length) {
        return getRandomString(CYRILLIC_CHARACTERS, length);
    }

    public static String getRandomOtherCharactersString(int length) {
        return getRandomString(OTHER_CHARACTERS, length);
    }

    private static String getRandomString(String chars, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }
}