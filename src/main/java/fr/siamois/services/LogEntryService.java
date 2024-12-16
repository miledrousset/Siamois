package fr.siamois.services;

import fr.siamois.infrastructure.repositories.LogEntryRepository;
import fr.siamois.models.LogEntry;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.auth.Person;
import fr.siamois.models.log.LogAction;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class LogEntryService {

    private final LogEntryRepository logEntryRepository;

    public LogEntryService(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    public void saveLog(Person loggedUser, OffsetDateTime date, LogAction action, Ark target) {
        LogEntry logEntry = new LogEntry();
        logEntry.setPerson(loggedUser);
        logEntry.setLogDate(date);
        logEntry.setMessage(action.toString());
        logEntry.setArk(target);

        logEntryRepository.save(logEntry);
    }

}
