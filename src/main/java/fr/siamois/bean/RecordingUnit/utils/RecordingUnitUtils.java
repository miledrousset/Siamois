package fr.siamois.bean.RecordingUnit.utils;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.PersonService;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import fr.siamois.utils.AuthenticatedUserUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Slf4j
@Component
public class RecordingUnitUtils {

    // Deps
    private final PersonService personService;
    private final RecordingUnitService recordingUnitService;


    private FieldConfigurationWrapper configurationWrapper;

    public RecordingUnitUtils(PersonService personService, RecordingUnitService recordingUnitService, FieldConfigurationService fieldConfigurationService, FieldService fieldService) {
        this.personService = personService;
        this.recordingUnitService = recordingUnitService;
    }

    public List<Person> completePerson(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        query = query.toLowerCase();
        return personService.findAllByNameLastnameContaining(query);
    }


    public RecordingUnit save(RecordingUnit recordingUnit,
                              FieldConfigurationWrapper configurationWrapper,
                              ConceptFieldDTO recordingUnitType,
                              LocalDate startDate,
                              LocalDate endDate) {

        // TODO : handle isLocalisationFromSIG and associated fields

        Vocabulary vocabulary = configurationWrapper.vocabularyConfig();
        if (vocabulary == null) vocabulary = configurationWrapper.vocabularyCollectionsConfig().get(0).getVocabulary();

        // handle dates
        if (startDate != null) {
            recordingUnit.setStartDate(localDateToOffsetDateTime(startDate));
        }
        if (endDate != null) {
            recordingUnit.setEndDate(localDateToOffsetDateTime(endDate));
        }

        return recordingUnitService.save(recordingUnit, vocabulary, recordingUnitType);

    }

    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
    }

    public OffsetDateTime localDateToOffsetDateTime(LocalDate localDate) {
        return localDate.atTime(LocalTime.NOON).atOffset(ZoneOffset.UTC);
    }
}
