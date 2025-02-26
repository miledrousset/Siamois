package fr.siamois.domain.utils;

import java.security.SecureRandom;
import java.util.Random;

public class CodeUtils {

    private CodeUtils() {}

    private static final Random RANDOM = new SecureRandom();

    private static char generateRandomChar() {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-";
        int randomIndex = RANDOM.nextInt(0, allowedChars.length());
        return allowedChars.charAt(randomIndex);
    }

    public static String generateCode(int maxLength) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < maxLength; i++) {
            code.append(generateRandomChar());
        }

        while (code.charAt(maxLength - 1) == '-' || code.charAt(0) == '-') {
            code.setCharAt(0, generateRandomChar());
            code.setCharAt(maxLength - 1, generateRandomChar());
        }

        return code.toString();
    }

}
