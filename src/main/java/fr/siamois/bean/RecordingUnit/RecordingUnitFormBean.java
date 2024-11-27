package fr.siamois.bean.RecordingUnit;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import lombok.Data;
import lombok.Getter;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardWidget;

import javax.faces.bean.SessionScoped;
import java.util.List;
import java.util.ArrayList;

@Named
@Data
@SessionScoped
public class RecordingUnitFormBean {
    private List<Event> events;

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

    @PostConstruct
    public void init() {
        events = new ArrayList<>();
        events.add(new Event("Anterior", "15/10/2020 10:30", "pi pi-arrow-circle-up", "#9C27B0", "game-controller.jpg"));
        events.add(new Event("Synchronous", "15/10/2020 14:00", "pi pi-sync", "#673AB7"));
        events.add(new Event("Posterior", "15/10/2020 16:15", "pi pi-arrow-circle-down", "#FF9800"));
    }

}
