package fr.siamois.utils;

public class CodeUtils {

    private static char generateRandomChar() {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-";
        int randomIndex = (int) (Math.random() * allowedChars.length());
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
