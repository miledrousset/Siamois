package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.infrastructure.api.dto.PurlInfoDTO;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import fr.siamois.models.UserInfo;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ConceptService {

    private final ConceptRepository conceptRepository;
    private final FieldConfigurationService fieldConfigurationService;
    private final ConceptApi conceptApi;
    private static final double SIMILARITY_CAP = 0.6;

    public ConceptService(ConceptRepository conceptRepository, FieldConfigurationService fieldConfigurationService, ConceptApi conceptApi) {
        this.conceptRepository = conceptRepository;
        this.fieldConfigurationService = fieldConfigurationService;
        this.conceptApi = conceptApi;
    }

    public Concept saveOrGetConcept(Concept concept) {
        Vocabulary vocabulary = concept.getVocabulary();
        Optional<Concept> optConcept = conceptRepository.findConceptByExternalIdIgnoreCase(vocabulary.getExternalVocabularyId(), concept.getExternalId());
        return optConcept.orElseGet(() -> conceptRepository.save(concept));
    }

    /**
     * Calculates the similarity (between 0 and 1) of the strings using Levenshtein distance algorithm
     * @param s1 First string
     * @param s2 Second String
     * @return A number between 0.0 and 1.0 representing the similarity of the two strings. The number is equal to 1 if the strings are the same.
     */
    private double stringSimilarity(String s1, String s2) {
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

    public List<Concept> fetchAutocomplete(UserInfo info, String fieldCode, String input) throws NoConfigForField {
        Concept parentConcept = fieldConfigurationService.findConfigurationForFieldCode(info, fieldCode);
        ConceptBranchDTO terms = conceptApi.fetchConceptsUnderTopTerm(parentConcept);
        List<Concept> result = new ArrayList<>();
        for (FullConceptDTO fullConcept : terms.getData().values()) {
            if (isNotParentConcept(fullConcept, parentConcept)) {
                PurlInfoDTO label = getPrefLabelOfLang(info, fullConcept);
                double similarity = stringSimilarity(label.getValue(), input);
                if (similarity >= SIMILARITY_CAP) {
                    result.add(createConceptFromDTO(parentConcept.getVocabulary(), label, fullConcept));
                }
            }
        }
        return result;
    }

    private Concept createConceptFromDTO(Vocabulary vocabulary, PurlInfoDTO label, FullConceptDTO conceptDTO) {
        Concept concept = new Concept();
        concept.setVocabulary(vocabulary);
        concept.setExternalId(conceptDTO.getIdentifier()[0].getValue());
        concept.setLabel(label.getValue());
        concept.setLangCode(label.getLang());
        return concept;
    }

    private static PurlInfoDTO getPrefLabelOfLang(UserInfo info,  FullConceptDTO fullConcept) {
        return Arrays.stream(fullConcept.getPrefLabel())
                .filter((purlInfoDTO -> purlInfoDTO.getLang().equalsIgnoreCase(info.getLang())))
                .findFirst()
                .orElse(fullConcept.getPrefLabel()[0]);
    }

    private static boolean isNotParentConcept(FullConceptDTO fullConcept, Concept parentConcept) {
        return !fullConcept.getIdentifier()[0].getValue().equalsIgnoreCase(parentConcept.getExternalId());
    }

}
