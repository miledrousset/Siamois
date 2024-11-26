package fr.siamois.services.ark;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class ArkGenerator {

    private static String serverNaanNumber = "666666";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

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
        return serverNaanNumber + "/" + sb.toString();
    }

}
