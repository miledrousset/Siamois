package fr.siamois.bean.RecordingUnit.utils;

import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.PersonService;
import fr.siamois.services.RecordingUnitService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@Slf4j
@Component
public class RecordingUnitUtils {

    // Deps
    private final PersonService personService;
    private final RecordingUnitService recordingUnitService;

    public RecordingUnitUtils(PersonService personService, RecordingUnitService recordingUnitService) {
        this.personService = personService;
        this.recordingUnitService = recordingUnitService;
    }

    public RecordingUnit save(RecordingUnit recordingUnit,
                              Concept typeConcept,
                              LocalDate startDate,
                              LocalDate endDate) {

        // TODO : handle isLocalisationFromSIG and associated fields

        // handle dates
        if (startDate != null) {
            recordingUnit.setStartDate(localDateToOffsetDateTime(startDate));
        }
        if (endDate != null) {
            recordingUnit.setEndDate(localDateToOffsetDateTime(endDate));
        }

        return recordingUnitService.save(recordingUnit, typeConcept);

    }

    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
    }

    public OffsetDateTime localDateToOffsetDateTime(LocalDate localDate) {
        return localDate.atTime(LocalTime.NOON).atOffset(ZoneOffset.UTC);
    }
}
