package fr.siamois.bean.RecordingUnit.utils;

import fr.siamois.models.auth.Person;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.auth.PersonService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.bean.ApplicationScoped;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

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

    public List<Person> completePerson(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        query = query.toLowerCase();
        return personService.findAllByNameLastnameContaining(query);
    }

    public RecordingUnit save(RecordingUnit recordingUnit, LocalDate startDate, LocalDate endDate) {

        log.error("im here");
        // TODO : handle isLocalisationFromSIG and associated fields

        // handle dates
        if (startDate != null) {
            recordingUnit.setStartDate(localDateToOffsetDateTime(startDate));
        }
        if (endDate != null) {
            recordingUnit.setEndDate(localDateToOffsetDateTime(endDate));
        }

        return recordingUnitService.save(recordingUnit);

    }

    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
    }

    public OffsetDateTime localDateToOffsetDateTime(LocalDate localDate) {
        return localDate.atTime(LocalTime.NOON).atOffset(ZoneOffset.UTC);
    }
}
