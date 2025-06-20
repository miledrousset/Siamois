package fr.siamois.domain.services.vocabulary;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.exceptions.api.NotSiamoisThesaurusException;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.GlobalFieldConfig;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullInfoDTO;
import fr.siamois.infrastructure.api.dto.PurlInfoDTO;
import fr.siamois.infrastructure.database.repositories.FieldRepository;
import fr.siamois.infrastructure.database.repositories.vocabulary.ConceptRepository;
import fr.siamois.domain.models.exceptions.ErrorProcessingExpansionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.*;

import static fr.siamois.utils.FullInfoDTOUtils.getPrefLabelOfLang;

@Slf4j
@Service
public class FieldConfigurationService {

    private static final IllegalStateException FIELD_CODE_NOT_FOUND = new IllegalStateException("Field code not found");
    private final ConceptApi conceptApi;
    private final FieldService fieldService;
    private final FieldRepository fieldRepository;
    private final ConceptRepository conceptRepository;
    private final ConceptService conceptService;

    private static final double SIMILARITY_CAP = 0.5;
    private final LabelService labelService;


    public FieldConfigurationService(ConceptApi conceptApi,
                                     FieldService fieldService,
                                     FieldRepository fieldRepository,
                                     ConceptRepository conceptRepository,
                                     ConceptService conceptService, LabelService labelService) {
        this.conceptApi = conceptApi;
        this.fieldService = fieldService;
        this.fieldRepository = fieldRepository;
        this.conceptRepository = conceptRepository;
        this.conceptService = conceptService;
        this.labelService = labelService;
    }

    private boolean containsFieldCode(FullInfoDTO conceptDTO) {
        return conceptDTO.getFieldcode().isPresent();
    }

    public Optional<GlobalFieldConfig> setupFieldConfigurationForInstitution(UserInfo info, Vocabulary vocabulary) throws NotSiamoisThesaurusException, ErrorProcessingExpansionException {
        ConceptBranchDTO conceptBranchDTO =  conceptApi.fetchFieldsBranch(vocabulary);
        GlobalFieldConfig config = createConfigOfThesaurus(conceptBranchDTO);
        if (config.isWrongConfig()) return Optional.of(config);

        for (FullInfoDTO conceptDTO : config.conceptWithValidFieldCode()) {
            Concept concept = conceptService.saveOrGetConceptFromFullDTO(vocabulary, conceptDTO);
            String fieldCode = conceptDTO.getFieldcode().orElseThrow(() -> FIELD_CODE_NOT_FOUND);

            int rowAffected = fieldRepository.updateConfigForFieldOfInstitution(info.getInstitution().getId(), fieldCode, concept.getId());
            if (rowAffected == 0) {
                fieldRepository.saveConceptForFieldOfInstitution(info.getInstitution().getId(), fieldCode, concept.getId());
            }
        }

        return Optional.empty();
    }

    private GlobalFieldConfig createConfigOfThesaurus(ConceptBranchDTO conceptBranchDTO) {
        final List<String> existingFieldCodes = fieldService.searchAllFieldCodes();
        final List<FullInfoDTO> allConceptsWithPotentialFieldCode = conceptBranchDTO.getData().values().stream()
                .filter(this::containsFieldCode)
                .toList();

        final List<String> missingFieldCode = existingFieldCodes.stream()
                .filter(fieldCode -> allConceptsWithPotentialFieldCode.stream()
                        .map(concept -> concept.getFieldcode().orElseThrow(() -> FIELD_CODE_NOT_FOUND).toUpperCase())
                        .noneMatch(fieldCode::equals))
                .toList();

        final List<FullInfoDTO> validConcept = allConceptsWithPotentialFieldCode.stream()
                .filter(concept -> {
                    String fieldCode = concept.getFieldcode().orElseThrow(() -> FIELD_CODE_NOT_FOUND).toUpperCase();
                    return existingFieldCodes.contains(fieldCode);
                })
                .toList();

        return new GlobalFieldConfig(missingFieldCode, validConcept);
    }

    public Optional<GlobalFieldConfig> setupFieldConfigurationForUser(UserInfo info, Vocabulary vocabulary) throws NotSiamoisThesaurusException, ErrorProcessingExpansionException {
        ConceptBranchDTO conceptBranchDTO =  conceptApi.fetchFieldsBranch(vocabulary);
        GlobalFieldConfig config = createConfigOfThesaurus(conceptBranchDTO);
        if (config.isWrongConfig()) return Optional.of(config);

        for (FullInfoDTO conceptDTO : config.conceptWithValidFieldCode()) {
            Concept concept = conceptService.saveOrGetConceptFromFullDTO(vocabulary, conceptDTO);
            String fieldCode = conceptDTO.getFieldcode().orElseThrow(() -> FIELD_CODE_NOT_FOUND);

            int rowAffected = fieldRepository.updateConfigForFieldOfUser(info.getInstitution().getId(),
                    info.getUser().getId(),
                    fieldCode,
                    concept.getId());
            if (rowAffected == 0) {
                fieldRepository.saveConceptForFieldOfUser(info.getInstitution().getId(),
                        info.getUser().getId(),
                        fieldCode,
                        concept.getId());
            }
        }

        return Optional.empty();
    }

    public Optional<String> findVocabularyUrlOfInstitution(Institution institution) {
        Optional<Concept> optConcept = conceptRepository
                .findTopTermConfigForFieldCodeOfInstitution(institution.getId(), SpatialUnit.CATEGORY_FIELD_CODE);
        if (optConcept.isEmpty()) return Optional.empty();
        Vocabulary vocabulary = optConcept.get().getVocabulary();
        return Optional.of(vocabulary.getBaseUri() + "/?idt=" + vocabulary.getExternalVocabularyId());
    }

    public Concept findConfigurationForFieldCode(UserInfo info, String fieldCode) throws NoConfigForFieldException {
        Optional<Concept> optConcept = conceptRepository
                .findTopTermConfigForFieldCodeOfUser(info.getInstitution().getId(),
                        info.getUser().getId(),
                        fieldCode);

        if (optConcept.isPresent()) return optConcept.get();

        optConcept = conceptRepository
                .findTopTermConfigForFieldCodeOfInstitution(info.getInstitution().getId(), fieldCode);

        if (optConcept.isEmpty())
            throw new NoConfigForFieldException(String.format("User '%s' from '%s' has no config for fieldCode '%s'",
                    info.getUser().getName(), info.getInstitution().getName(), fieldCode));

        return optConcept.get();
    }

    public String getUrlOfConcept(Concept c) {
        return c.getVocabulary().getBaseUri()+"/?idc="+c.getExternalId()+"&idt="+c.getVocabulary().getExternalVocabularyId();
    }

    public String getUrlForFieldCode(UserInfo info, String fieldCode) {
        try {
            return getUrlOfConcept(findConfigurationForFieldCode(info, fieldCode));
        } catch(NoConfigForFieldException e) {
            return null;
        }
    }

    public List<Concept> fetchConceptChildrenAutocomplete(UserInfo info, Concept concept, String input) {
        List<Concept> candidates;
        try {
            candidates = conceptService.findDirectSubConceptOf(concept);
        } catch (ErrorProcessingExpansionException e) {
            log.error("Error fetching children concepts for autocomplete", e);
            return List.of();
        }
        Map<Concept, ConceptLabel> labels = new HashMap<>();

        if (StringUtils.isEmpty(input)) return candidates;

        List<Concept> result = new ArrayList<>();

        input = input.toLowerCase();

        for (Concept c : candidates) {
            ConceptLabel label = labelService.findLabelOf(c, info.getLang());
            labels.put(c, label);
            if (label.getValue().toLowerCase().contains(input)) {
                result.add(c);
            }
        }

        if (result.isEmpty()) {
            for (Concept c : candidates) {
                ConceptLabel label = labels.get(c);
                double similarity = stringSimilarity(label.getValue().toLowerCase(), input);
                if (similarity >= SIMILARITY_CAP) {
                    result.add(c);
                }
            }
        }

        return result;
    }

    public List<Concept> fetchAutocomplete(UserInfo info, Concept parentConcept, String input) {
        if (StringUtils.isEmpty(input)) return fetchAllValues(parentConcept);

        ConceptBranchDTO terms;
        try {
            terms = conceptApi.fetchConceptsUnderTopTerm(parentConcept);
        } catch (ErrorProcessingExpansionException e) {
            log.error("Error fetching concepts for autocomplete", e);
            return List.of();
        }
        List<Concept> result = new ArrayList<>();

        input = input.toLowerCase();

        for (FullInfoDTO fullConcept : terms.getData().values()) {
            if (isNotParentConcept(fullConcept, parentConcept)) {
                PurlInfoDTO label = getPrefLabelOfLang(info, fullConcept);
                if (label.getValue().toLowerCase().contains(input)) {
                    result.add(conceptService.saveOrGetConceptFromFullDTO(parentConcept.getVocabulary(), fullConcept));
                }
            }
        }

        if (result.isEmpty()) {
            for (FullInfoDTO fullInfoDTO : terms.getData().values()) {
                PurlInfoDTO label = getPrefLabelOfLang(info, fullInfoDTO);
                double similarity = stringSimilarity(label.getValue().toLowerCase(), input);
                if (similarity >= SIMILARITY_CAP) {
                    result.add(conceptService.saveOrGetConceptFromFullDTO(parentConcept.getVocabulary(), fullInfoDTO));
                }
            }
        }

        return result;
    }

    public List<Concept> fetchAutocomplete(UserInfo info, String fieldCode, String input) throws NoConfigForFieldException {
        Concept parentConcept = findConfigurationForFieldCode(info, fieldCode);
        return fetchAutocomplete(info, parentConcept, input);
    }

    /**
     * Calculates the similarity (between 0 and 1) of the strings using Levenshtein distance algorithm
     * @param s1 First string
     * @param s2 Second String
     * @return A number between 0.0 and 1.0 representing the similarity of the two strings. The number is equal to 1 if the strings are the same.
     */
    double stringSimilarity(String s1, String s2) {
        String longer = s1;
        String shorter = s2;
        if (s1.length() < shorter.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        int distance = LevenshteinDistance.getDefaultInstance().apply(longer, shorter);
        return (longerLength - distance) / (double) longerLength;
    }

    public List<Concept> fetchAllValues(Concept parent) {
        ConceptBranchDTO terms;
        try {
            terms = conceptApi.fetchConceptsUnderTopTerm(parent);
        } catch (ErrorProcessingExpansionException e) {
            log.error("Error fetching concepts under top term", e);
            return List.of();
        }
        List<Concept> result = new ArrayList<>();
        for (FullInfoDTO fullConcept : terms.getData().values()) {
            if (isNotParentConcept(fullConcept, parent)) {
                result.add(conceptService.saveOrGetConceptFromFullDTO(parent.getVocabulary(), fullConcept));
            }
        }
        return result;
    }


    private static boolean isNotParentConcept(FullInfoDTO fullConcept, Concept parentConcept) {
        return !fullConcept.getIdentifier()[0].getValue().equalsIgnoreCase(parentConcept.getExternalId());
    }

    public boolean hasUserConfig(UserInfo info) {
        return fieldRepository.hasUserConfig(info.getUser().getId(), info.getInstitution().getId());
    }

}
