package fr.siamois.bean.RecordingUnit;

import fr.siamois.infrastructure.repositories.PersonRepository;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.auth.Person;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.ark.ArkGenerator;
import fr.siamois.services.auth.PersonDetailsService;
import fr.siamois.services.vocabulary.VocabularyService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import static java.time.OffsetDateTime.now;

@Data
@Slf4j
@SessionScoped
@Component
public class RecordingUnitFormBean implements Serializable {
    private List<Event> events;

    // Deps
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;

    // TODO : remove below
    private final ArkServerRepository arkServerRepository;
    private final PersonDetailsService personDetailsService;
    private final VocabularyService vocabularyService;
    // TODO : end to remove

    private RecordingUnit recordingUnit;

    @Getter
    public static class Event {

        private String status;
        private String date;
        private String icon;
        private String color;
        private String image;

        public Event() {
        }

        public Event(String status, String date, String icon, String color) {
            this.status = status;
            this.date = date;
            this.icon = icon;
            this.color = color;
        }

        public Event(String status, String date, String icon, String color, String image) {
            this.status = status;
            this.date = date;
            this.icon = icon;
            this.color = color;
            this.image = image;
        }

    }

    public void save() {
        try {
            log.info(String.valueOf(this.recordingUnit));
            this.recordingUnit = recordingUnitService.save(recordingUnit);
            log.info("Recording unit saved");
        }
        catch(Exception e) {
            log.error(e.getMessage());
        }
    }


    public RecordingUnitFormBean(RecordingUnitService recordingUnitService,
                                 ActionUnitService  actionUnitService, ArkServerRepository arkServerRepository, PersonDetailsService personDetailsService, VocabularyService vocabularyService) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.arkServerRepository = arkServerRepository;
        this.personDetailsService = personDetailsService;
        this.vocabularyService = vocabularyService;
    }

    @PostConstruct
    public void init() {

        // TODO : clean below, properly get concept
        Concept c = new Concept();
        c.setLabel("US");
        c.setVocabulary(this.vocabularyService.findVocabularyById(14));
        this.recordingUnit = new RecordingUnit();
        this.recordingUnit.setType(c);
        this.recordingUnit.setDescription("Nouvelle description");
        this.recordingUnit.setName("Nouvelle unité d'enregistrement");
        this.recordingUnit.setRecordingUnitDate(now());
        // Below is hardcoded but it should not be. TODO
        ActionUnit actionUnit = this.actionUnitService.findById(4);
        this.recordingUnit.setActionUnit(actionUnit);
        // Todo : properly generate ARK
        Ark ark = new Ark();
        ark.setArkServer(arkServerRepository.findArkServerByServerArkUri("http://localhost:8099/siamois").orElse(null));
        ark.setArkId(ArkGenerator.generateArk());
        Person p = new Person();
        p.setMail("dummmyMail@gmail.com");
        p.setUsername("dummmy");
        p.setPassword("dummmy");
        this.recordingUnit.setArk(ark);
        this.recordingUnit.setAuthor(p);
        log.info(this.recordingUnit.getActionUnit().toString());


        events = new ArrayList<>();
        events.add(new Event("Anterior", "15/10/2020 10:30", "pi pi-arrow-circle-up", "#9C27B0", "game-controller.jpg"));
        events.add(new Event("Synchronous", "15/10/2020 14:00", "pi pi-sync", "#673AB7"));
        events.add(new Event("Posterior", "15/10/2020 16:15", "pi pi-arrow-circle-down", "#FF9800"));
    }

}
