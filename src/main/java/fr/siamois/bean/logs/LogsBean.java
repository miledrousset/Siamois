package fr.siamois.bean.logs;

import fr.siamois.bean.SessionSettings;
import fr.siamois.models.auth.Person;
import fr.siamois.models.history.HistoryOperation;
import fr.siamois.services.HistoryService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Setter
@Getter
@Component
@SessionScoped
public class LogsBean implements Serializable {

    private final HistoryService historyService;
    private final SessionSettings sessionSettings;

    private List<HistoryOperation> operations;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LocalDateTime endOfToday = dateEndOfToday();

    private LocalDateTime vEndDateTime = LocalDateTime.now(ZoneId.systemDefault());
    private LocalDateTime vStartDateTime = dayBeforeAtMidnight(vEndDateTime);

    public LogsBean(HistoryService historyService, SessionSettings sessionSettings) {
        this.historyService = historyService;
        this.sessionSettings = sessionSettings;
    }

    private LocalDateTime dayBeforeAtMidnight(LocalDateTime dateTime) {
        return dateTime.minusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    private LocalDateTime dateEndOfToday() {
        return LocalDateTime.now(ZoneId.systemDefault())
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(0);
    }

    public void init() {
        refreshOperation();
    }

    public void refreshOperation() {
        log.trace("Date changed. Is now {} to {}", vStartDateTime.toString(), vEndDateTime.toString());
        Person authenticatedUser = sessionSettings.getAuthenticatedUser();
        ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(vStartDateTime);
        OffsetDateTime start = OffsetDateTime.of(vStartDateTime, offset);
        OffsetDateTime end = OffsetDateTime.of(vEndDateTime, offset);
        operations = historyService.findAllOperationsOfUserBetween(authenticatedUser, start, end);
    }

}
