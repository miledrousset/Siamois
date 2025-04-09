package fr.siamois.domain.services.vocabulary;

import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullInfoDTO;
import fr.siamois.infrastructure.api.dto.PurlInfoDTO;
import fr.siamois.infrastructure.database.repositories.vocabulary.ConceptRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ConceptService {

    private final ConceptRepository conceptRepository;
    private final ConceptApi conceptApi;
    private final LabelService labelService;

    public ConceptService(ConceptRepository conceptRepository, ConceptApi conceptApi, LabelService labelService) {
        this.conceptRepository = conceptRepository;
        this.conceptApi = conceptApi;
        this.labelService = labelService;
    }

    public Concept saveOrGetConcept(Concept concept) {
        Vocabulary vocabulary = concept.getVocabulary();
        Optional<Concept> optConcept = conceptRepository.findConceptByExternalIdIgnoreCase(vocabulary.getExternalVocabularyId(), concept.getExternalId());
        return optConcept.orElseGet(() -> conceptRepository.save(concept));
    }

    public Concept saveOrGetConceptFromFullDTO(Vocabulary vocabulary, FullInfoDTO conceptDTO) {
        Optional<Concept> optConcept = conceptRepository
                .findConceptByExternalIdIgnoreCase(
                        vocabulary.getExternalVocabularyId(),
                        conceptDTO.getIdentifier()[0].getValue()
                );

        if (optConcept.isPresent()) {
            updateAllLabelsFromDTO(optConcept.get(), conceptDTO);
            return optConcept.get();
        }

        Concept concept = new Concept();
        concept.setVocabulary(vocabulary);
        concept.setExternalId(conceptDTO.getIdentifier()[0].getValue());

        concept = conceptRepository.save(concept);

        updateAllLabelsFromDTO(concept, conceptDTO);

        return concept;
    }

    public void updateAllLabelsFromDTO(Concept savedConcept, FullInfoDTO conceptDto) {
        if (conceptDto.getPrefLabel() != null) {
            for (PurlInfoDTO label : conceptDto.getPrefLabel()) {
                labelService.updateLabel(savedConcept, label.getLang(), label.getValue());
            }
        }
    }

    public List<Concept> findDirectSubConceptOf(Concept concept) {
        ConceptBranchDTO branch = conceptApi.fetchDownExpansion(concept.getVocabulary(), concept.getExternalId());
        List<Concept> result = new ArrayList<>();
        if (branch.isEmpty()) {
            return result;
        }

        FullInfoDTO parentConcept = branch.getData().values().stream()
                .filter(dto -> concept.getExternalId().equalsIgnoreCase(dto.getIdentifier()[0].getValue()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No concept found for " + concept.getExternalId()));

        if (parentConcept.getNarrower() == null) return result;

        List<FullInfoDTO> childs = Arrays.stream(parentConcept.getNarrower())
                .filter((purlInfoDTO -> branch.getData().containsKey(purlInfoDTO.getValue())))
                .map((purlInfoDTO -> branch.getData().get(purlInfoDTO.getValue())))
                .toList();

        for (FullInfoDTO child : childs) {
            Concept newConcept = saveOrGetConceptFromFullDTO(concept.getVocabulary(), child);
            result.add(newConcept);
        }

        return result;
    }

    public Object findAllById(List<Long> conceptIds) {
        return conceptRepository.findAllById(conceptIds);
    }
}
