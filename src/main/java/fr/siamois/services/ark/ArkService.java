package fr.siamois.services.ark;

import fr.siamois.infrastructure.repositories.ArkRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.exceptions.ark.NoArkConfigException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class ArkService {

    private static final String VALID_CHAR;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_GENERATIONS = 1000;

    static {
        StringBuilder builder = new StringBuilder();
        for (char c = 'a'; c <= 'z'; c = (char) (c + 1)) {
            builder.append(c);
        }
        for (char c = '0'; c <= '9'; c = (char) (c + 1)) {
            builder.append(c);
        }
        VALID_CHAR = builder.toString();
    }

    private final NoidCheckService noidCheckService;
    private final ArkRepository arkRepository;

    public ArkService(NoidCheckService noidCheckService, ArkRepository arkRepository) {
        this.noidCheckService = noidCheckService;
        this.arkRepository = arkRepository;
    }

    private char getRandomChar() {
        return VALID_CHAR.charAt(RANDOM.nextInt(VALID_CHAR.length()));
    }

    private String randomArkQualifier(int length) {
        StringBuilder builder = new StringBuilder();
        while (builder.length() < length) {
            builder.append(getRandomChar());
        }
        String tmp = builder.toString();
        String controlChar = noidCheckService.calculateCheckDigit(tmp);

        builder.append("-").append(controlChar);

        return builder.toString();
    }

    public boolean qualifierNotExistInInstitution(Institution institution, String qualifier) {
        return !arkRepository.existsByInstitutionAndQualifier(institution.getId(), qualifier);
    }

    public Ark generateAndSave(Institution institution) throws NoArkConfigException {
        if (institution.getArkNaan() == null) {
            throw new NoArkConfigException(institution);
        }

        boolean isValid;
        int interationCount = 0;
        String randomArkQualifier;

        do {
            randomArkQualifier = randomArkQualifier(institution.getArkSize());
            isValid = qualifierNotExistInInstitution(institution, randomArkQualifier);
            interationCount++;
        } while (!isValid && interationCount < MAX_GENERATIONS);

        if (interationCount == MAX_GENERATIONS)
            throw new IllegalStateException("Can't generate ARK after " + MAX_GENERATIONS + " generations.");

        Ark ark = new Ark();
        ark.setCreatingInstitution(institution);
        ark.setQualifier(randomArkQualifier);

        return arkRepository.save(ark);
    }

}
