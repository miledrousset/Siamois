package fr.siamois.domain.services.vocabulary;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullInfoDTO;
import fr.siamois.infrastructure.api.dto.LabelDTO;
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

    public ConceptService(ConceptRepository conceptRepository, ConceptApi conceptApi) {
        this.conceptRepository = conceptRepository;
        this.conceptApi = conceptApi;
    }

    public Concept saveOrGetConcept(Concept concept) {
        Vocabulary vocabulary = concept.getVocabulary();
        Optional<Concept> optConcept = conceptRepository.findConceptByExternalIdIgnoreCase(vocabulary.getExternalVocabularyId(), concept.getExternalId());
        return optConcept.orElseGet(() -> conceptRepository.save(concept));
    }


    public List<Concept> findAllConceptsByInstitution(Institution institution) {
        return conceptRepository.findAllBySpatialUnitOfInstitution(institution.getId());
    }

    public Concept saveOrGetConceptFromFullDTO(UserInfo info, Vocabulary vocabulary, FullInfoDTO conceptDTO) {
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

    private Optional<LabelDTO> findLabelOfLang(FullInfoDTO conceptDTO, String lang) {
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

    private LabelDTO firstAvailableLabel(FullInfoDTO fullInfoDTO) {
        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setTitle(fullInfoDTO.getPrefLabel()[0].getValue());
        labelDTO.setLang(fullInfoDTO.getPrefLabel()[0].getLang());
        return labelDTO;
    }



    public List<Concept> findDirectSubConceptOf(UserInfo userInfo, Concept concept) {
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
            Concept newConcept = saveOrGetConceptFromFullDTO(userInfo, concept.getVocabulary(), child);
            result.add(newConcept);
        }

        return result;
    }

    public Object findAllById(List<Long> conceptIds) {
        return conceptRepository.findAllById(conceptIds);
    }
}
