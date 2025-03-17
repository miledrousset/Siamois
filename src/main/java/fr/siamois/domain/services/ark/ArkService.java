package fr.siamois.domain.services.ark;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.exceptions.ark.NoArkConfigException;
import fr.siamois.domain.models.exceptions.ark.TooManyGenerationsException;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.infrastructure.database.repositories.ArkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.SecureRandom;

@Service
public class ArkService {

    private static final String VALID_CHAR_STR = "0123456789bcdfghjklmnpqrstvwxz";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_GENERATIONS = 100;

    private final NoidCheckService noidCheckService;
    private final ArkRepository arkRepository;
    private final InstitutionService institutionService;

    private ServletUriComponentsBuilder builder;

    @Autowired
    public ArkService(NoidCheckService noidCheckService,
                      ArkRepository arkRepository,
                      InstitutionService institutionService) {
        this.noidCheckService = noidCheckService;
        this.arkRepository = arkRepository;
        this.institutionService = institutionService;
    }

    public ArkService(NoidCheckService noidCheckService,
                      ArkRepository arkRepository,
                      InstitutionService institutionService,
                      ServletUriComponentsBuilder builder) {
        this.noidCheckService = noidCheckService;
        this.arkRepository = arkRepository;
        this.institutionService = institutionService;
        this.builder = builder;
    }

    private char getRandomChar() {
        return VALID_CHAR_STR.charAt(RANDOM.nextInt(VALID_CHAR_STR.length()));
    }

    private String randomArkQualifier(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() < length) {
            stringBuilder.append(getRandomChar());
        }
        String tmp = stringBuilder.toString();
        String controlChar = noidCheckService.calculateCheckDigit(tmp);

        stringBuilder.append("-").append(controlChar);

        return stringBuilder.toString();
    }

    public boolean qualifierNotExistInInstitution(Institution institution, String qualifier) {
        return arkRepository.findByInstitutionAndQualifier(institution.getId(), qualifier).isEmpty();
    }

    public Ark generateAndSave(InstitutionSettings settings) throws NoArkConfigException, TooManyGenerationsException {
        Institution institution = settings.getInstitution();
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

    public String getUriOf(Ark ark) {
        InstitutionSettings settings = institutionService.createOrGetSettingsOf(ark.getCreatingInstitution());
        if (!settings.hasEnabledArkConfig()) {
            throw new IllegalStateException(String.format("Institution n°%s should have ark settings.", settings.getId()));
        }

        String qualifier = ark.getQualifier();
        if (Boolean.TRUE.equals(settings.getArkIsUppercase()))
            qualifier = qualifier.toUpperCase();

        ServletUriComponentsBuilder currentBuilder;
        if (builder == null) {
            currentBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
        } else {
            currentBuilder = builder.cloneBuilder();
        }

        return currentBuilder
                .path("/api/ark:/")
                .path(settings.getArkNaan())
                .path("/")
                .path(qualifier)
                .toUriString();
    }



}
