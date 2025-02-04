package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import fr.siamois.models.FieldCode;
import fr.siamois.models.UserInfo;
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

}
