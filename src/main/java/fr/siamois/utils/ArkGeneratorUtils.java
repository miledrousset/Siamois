package fr.siamois.utils;

import java.security.SecureRandom;

/**
 * Service to generate ARKs.
 * @author Julien Linget
 */
public class ArkGeneratorUtils {

    private static final String SERVER_NAAN_NUMBER = "666666";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private ArkGeneratorUtils() {}

    /**
     * DEVELOPMENT PURPOSES ONLY
     * @return a random ARK
     */
    public static String generateArk() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return SERVER_NAAN_NUMBER + "/" + sb.toString();
    }

}
