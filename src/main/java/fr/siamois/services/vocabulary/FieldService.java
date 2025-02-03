package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.repositories.FieldRepository;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import fr.siamois.models.FieldCode;
import fr.siamois.models.TraceInfo;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Service to handle the fields in the application.
 *
 * @author Julien Linget
 */
@Slf4j
@Service
@Transactional
public class FieldService {

    private final ConceptRepository conceptRepository;
    private final FieldRepository fieldRepository;

    public FieldService(ConceptRepository conceptRepository, FieldRepository fieldRepository) {
        this.conceptRepository = conceptRepository;
        this.fieldRepository = fieldRepository;
    }

    public List<String> searchAllFieldCodes() {
        Reflections reflections = new Reflections("fr.siamois.models", Scanners.FieldsAnnotated);
        Set<Field> fieldsWithFieldCode = reflections.getFieldsAnnotatedWith(FieldCode.class);
        List<String> fieldCodes = new ArrayList<>();

        for (Field field : fieldsWithFieldCode) {
            if (isValidFieldCode(field)) {
                try {
                    String fieldCode = (String) field.get(null);
                    fieldCodes.add(fieldCode.toUpperCase());
                } catch (IllegalAccessException e) {
                    log.error("Error while searching for field code {}", field.getName());
                }
            }
        }

        return fieldCodes;
    }

    private static boolean isValidFieldCode(Field field) {
        return field.getType().equals(String.class) &&
                Modifier.isStatic(field.getModifiers()) &&
                Modifier.isFinal(field.getModifiers());
    }

    private Optional<LabelDTO> findLabelOfLang(FullConceptDTO conceptDTO, String lang) {
        if (lang == null) return Optional.empty();

        return Arrays.stream(conceptDTO.getPrefLabel())
                .filter(purlInfoDTO -> purlInfoDTO.getLang().equals(lang))
                .map((elt) -> {
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

    public Concept createOrGetConceptFromFullDTO(TraceInfo info, Vocabulary vocabulary, FullConceptDTO conceptDTO) {
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

    public fr.siamois.models.Field createOrGetFieldFromCode(TraceInfo info, String fieldCode) {
        Optional<fr.siamois.models.Field> optField = fieldRepository.findByUserAndFieldCode(info.getUser(), fieldCode);
        if (optField.isPresent()) return optField.get();

        fr.siamois.models.Field field = new fr.siamois.models.Field();
        field.setFieldCode(fieldCode);
        field.setUser(info.getUser());

        return fieldRepository.save(field);
    }
}
