package fr.siamois.services.ark;

import fr.siamois.infrastructure.repositories.ArkRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.exceptions.ark.NoArkConfigException;
import fr.siamois.models.exceptions.ark.TooManyGenerationsException;
import fr.siamois.models.settings.InstitutionSettings;
import fr.siamois.services.InstitutionService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class ArkService {

    private static final String VALID_CHAR_STR = "0123456789bcdfghjklmnpqrstvwxz";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_GENERATIONS = 5000;

    private final NoidCheckService noidCheckService;
    private final ArkRepository arkRepository;
    private final InstitutionService institutionService;

    public ArkService(NoidCheckService noidCheckService, ArkRepository arkRepository, InstitutionService institutionService) {
        this.noidCheckService = noidCheckService;
        this.arkRepository = arkRepository;
        this.institutionService = institutionService;
    }

    private char getRandomChar() {
        return VALID_CHAR_STR.charAt(RANDOM.nextInt(VALID_CHAR_STR.length()));
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

    public Ark generateAndSave(Institution institution) throws NoArkConfigException, TooManyGenerationsException {
        InstitutionSettings settings = institutionService.findSettingsOf(institution);
        if (settings.getArkNaan() == null) {
            throw new NoArkConfigException(institution);
        }

        boolean isValid;
        int interationCount = 0;
        String randomArkQualifier;

        do {
            randomArkQualifier = randomArkQualifier(settings.getArkSize());
            isValid = qualifierNotExistInInstitution(institution, randomArkQualifier);
            interationCount++;
        } while (!isValid && interationCount < MAX_GENERATIONS);

        if (interationCount == MAX_GENERATIONS) {
            throw new TooManyGenerationsException(MAX_GENERATIONS, institution);
        }

        Ark ark = new Ark();
        ark.setCreatingInstitution(institution);
        ark.setQualifier(randomArkQualifier);

        return arkRepository.save(ark);
    }

}
