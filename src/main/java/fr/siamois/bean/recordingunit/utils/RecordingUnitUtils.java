package fr.siamois.bean.recordingunit.utils;

import fr.siamois.bean.LangBean;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.PersonService;
import fr.siamois.services.RecordingUnitService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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

    public String save(RecordingUnit recordingUnit, LangBean langBean) {
        try {

            // Return page with id
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Info",
                            langBean.msg("recordingunit.created", recordingUnit.getIdentifier())));

            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            return "/pages/recordingUnit/recordingUnit?faces-redirect=true&id=" + recordingUnit.getId().toString();

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("recordingunit.creationfailed", recordingUnit.getIdentifier())));

            log.error("Error while saving: {}", e.getMessage());
            // todo : add error message
        }
        return null;
    }
}
