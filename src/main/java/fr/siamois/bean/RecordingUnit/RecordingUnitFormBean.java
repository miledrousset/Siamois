package fr.siamois.bean.RecordingUnit;

import fr.siamois.models.ActionUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.ark.ArkGenerator;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.List;
import java.util.ArrayList;

@Data
@Slf4j
@SessionScoped
@Component
public class RecordingUnitFormBean {
    private List<Event> events;

    // Deps
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;

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
            this.recordingUnit = recordingUnitService.save(recordingUnit);
            log.info("Recording unit saved");
        }
        catch(Exception e) {
            log.error(e.getMessage());
        }
    }


    public RecordingUnitFormBean(RecordingUnitService recordingUnitService,
                                 ActionUnitService  actionUnitService) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
    }

    @PostConstruct
    public void init() {
        Concept c = new Concept();
        c.setLabel("US");
        recordingUnit = new RecordingUnit();
        recordingUnit.setType(c);
        recordingUnit.setName("Nouvelle unité d'enregistrement");
        // Below is hardcoded but it should not be. TODO
        ActionUnit actionUnit = this.actionUnitService.findById(4);
        recordingUnit.setActionUnit(actionUnit);
        // Todo : properly generate ARK
        Ark ark = new Ark();
//        ark.setArkServer();
//        ark.setArkId(ArkGenerator.generateArk());


        events = new ArrayList<>();
        events.add(new Event("Anterior", "15/10/2020 10:30", "pi pi-arrow-circle-up", "#9C27B0", "game-controller.jpg"));
        events.add(new Event("Synchronous", "15/10/2020 14:00", "pi pi-sync", "#673AB7"));
        events.add(new Event("Posterior", "15/10/2020 16:15", "pi pi-arrow-circle-down", "#FF9800"));
    }

}
