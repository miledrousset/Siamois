package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import fr.siamois.models.UserInfo;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class ConceptService {

    private final ConceptRepository conceptRepository;

    public ConceptService(ConceptRepository conceptRepository) {
        this.conceptRepository = conceptRepository;
    }

    public Concept  saveOrGetConcept(Concept concept) {
        Vocabulary vocabulary = concept.getVocabulary();
        Optional<Concept> optConcept = conceptRepository.findConceptByExternalIdIgnoreCase(vocabulary.getExternalVocabularyId(), concept.getExternalId());
        return optConcept.orElseGet(() -> conceptRepository.save(concept));
    }

    public Concept saveOrGetConceptFromFullDTO(UserInfo info, Vocabulary vocabulary, FullConceptDTO conceptDTO) {
        Optional<Concept> optConcept = conceptRepository
                .findConceptByExternalIdIgnoreCase(
                        vocabulary.getExternalVocabularyId(),
                        conceptDTO.getIdentifier()[0].getValue()
                );
        if (optConcept.isPresent()) return optConcept.get();

        Concept concept = new Concept();
        LabelDTO labelDTO = findLabelOfLang(conceptDTO, info.getLang()).orElse(firstAvailableLabel(conceptDTO));
        concept.setLabel(labelDTO.getTitle());
        concept.setLangCode(labelDTO.getLang());
        concept.setVocabulary(vocabulary);
        concept.setExternalId(conceptDTO.getIdentifier()[0].getValue());

        return conceptRepository.save(concept);
    }

    private Optional<LabelDTO> findLabelOfLang(FullConceptDTO conceptDTO, String lang) {
        if (lang == null) return Optional.empty();

        return Arrays.stream(conceptDTO.getPrefLabel())
                .filter(purlInfoDTO -> purlInfoDTO.getLang().equals(lang))
                .map(elt -> {
                    LabelDTO labelDTO = new LabelDTO();
                    labelDTO.setTitle(elt.getValue());
                    labelDTO.setLang(elt.getLang());
                    return labelDTO;
                })
                .findFirst();
    }

    private LabelDTO firstAvailableLabel(FullConceptDTO fullConceptDTO) {
        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setTitle(fullConceptDTO.getPrefLabel()[0].getValue());
        labelDTO.setLang(fullConceptDTO.getPrefLabel()[0].getLang());
        return labelDTO;
    }

}
