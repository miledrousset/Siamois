package fr.siamois.ui.bean.logs;

import fr.siamois.domain.models.events.InstitutionChangeEvent;
import fr.siamois.domain.models.history.HistoryOperation;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Setter
@Getter
@Component
@SessionScoped
public class LogsBean implements Serializable {

    private final transient HistoryService historyService;
    private final SessionSettingsBean sessionSettingsBean;

    private transient List<HistoryOperation> operations;

    private final LocalDateTime endOfToday = dateEndOfCurrentDay();

    private LocalDateTime vEndDateTime = LocalDateTime.now(ZoneId.systemDefault());
    private LocalDateTime vStartDateTime = dayBeforeAtMidnight(vEndDateTime);

    public LogsBean(HistoryService historyService, SessionSettingsBean sessionSettingsBean) {
        this.historyService = historyService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    private LocalDateTime dayBeforeAtMidnight(LocalDateTime dateTime) {
        return dateTime.minusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    private LocalDateTime dateEndOfCurrentDay() {
        return LocalDateTime.now(ZoneId.systemDefault())
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(0);
    }

    public void init() {
        refreshOperation();
    }

    @EventListener(InstitutionChangeEvent.class)
    public void refreshOperation() {
        log.trace("Date changed. Is now {} to {}", vStartDateTime.toString(), vEndDateTime.toString());
        ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(vStartDateTime);
        OffsetDateTime start = OffsetDateTime.of(vStartDateTime, offset);
        OffsetDateTime end = OffsetDateTime.of(vEndDateTime, offset);
        try {
            operations = historyService.findAllOperationsOfUserAndTeamBetween(sessionSettingsBean.getUserInfo(), start, end);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public String formatDate(OffsetDateTime offsetDateTime) {
        return DateUtils.formatOffsetDateTime(offsetDateTime);
    }

}
