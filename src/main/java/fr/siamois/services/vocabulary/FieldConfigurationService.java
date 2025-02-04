package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.infrastructure.repositories.FieldRepository;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import fr.siamois.models.Field;
import fr.siamois.models.UserInfo;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.GlobalFieldConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FieldConfigurationService {

    private final ConceptApi conceptApi;
    private final FieldService fieldService;
    private final FieldRepository fieldRepository;
    private final ConceptRepository conceptRepository;

    public FieldConfigurationService(ConceptApi conceptApi, FieldService fieldService, FieldRepository fieldRepository, ConceptRepository conceptRepository) {
        this.conceptApi = conceptApi;
        this.fieldService = fieldService;
        this.fieldRepository = fieldRepository;
        this.conceptRepository = conceptRepository;
    }

    private boolean containsFieldCode(FullConceptDTO conceptDTO) {
        return conceptDTO.getFieldcode().isPresent();
    }

    public Optional<GlobalFieldConfig> setupFieldConfigurationForInstitution(UserInfo info, Vocabulary vocabulary) {
        ConceptBranchDTO conceptBranchDTO =  conceptApi.fetchFieldsBranch(vocabulary);
        GlobalFieldConfig config = createConfigOfThesaurus(conceptBranchDTO);
        if (config.isWrongConfig()) return Optional.of(config);

        for (FullConceptDTO conceptDTO : config.conceptWithValidFieldCode()) {
            Concept concept = fieldService.createOrGetConceptFromFullDTO(info, vocabulary, conceptDTO);
            String fieldCode = conceptDTO.getFieldcode().orElseThrow(() -> new IllegalStateException("Field code not found"));
            Field field = fieldService.createOrGetFieldFromCode(info, fieldCode);

            fieldRepository.saveConceptForFieldOfInstitution(info.getInstitution().getId(), field.getId(), concept.getId());
        }

        return Optional.empty();
    }

    private GlobalFieldConfig createConfigOfThesaurus(ConceptBranchDTO conceptBranchDTO) {
        final List<String> existingFieldCodes = fieldService.searchAllFieldCodes();
        final List<FullConceptDTO> allConceptsWithPotentialFieldCode = conceptBranchDTO.getData().values().stream()
                .filter(this::containsFieldCode)
                .toList();

        final List<String> missingFieldCode = existingFieldCodes.stream()
                .filter(fieldCode -> allConceptsWithPotentialFieldCode.stream()
                        .map(concept -> concept.getFieldcode().orElseThrow(() -> new IllegalStateException("Field code not found")).toUpperCase())
                        .noneMatch(fieldCode::equals))
                .toList();

        final List<FullConceptDTO> validConcept = allConceptsWithPotentialFieldCode.stream()
                .filter(concept -> {
                    String fieldCode = concept.getFieldcode().orElseThrow(() -> new IllegalStateException("Field code not found")).toUpperCase();
                    return existingFieldCodes.contains(fieldCode);
                })
                .toList();

        return new GlobalFieldConfig(missingFieldCode, validConcept);
    }

    public void setupFieldConfigurationForUser(UserInfo info, String fieldCode, Concept topTerm) {
        // TODO: Field configuration for user
    }

    public Concept findConfigurationForFieldCode(UserInfo info, String fieldCode) throws NoConfigForField {
        Optional<Concept> optConcept = conceptRepository
                .findTopTermConfigForFieldCodeOfUser(info.getInstitution().getId(),
                        info.getUser().getId(),
                        fieldCode);

        if (optConcept.isPresent()) return optConcept.get();

        optConcept = conceptRepository
                .findTopTermConfigForFieldCodeOfInstitution(info.getInstitution().getId(), fieldCode);

        if (optConcept.isEmpty())
            throw new NoConfigForField(String.format("User %s from %s has no config for fieldCode %s",
                    info.getUser().getName(), info.getInstitution().getName(), fieldCode));

        return optConcept.get();
    }

}
