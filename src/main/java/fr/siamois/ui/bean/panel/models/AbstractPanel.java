package fr.siamois.ui.bean.panel.models;

import lombok.Data;
import lombok.Getter;

@Data
public abstract class AbstractPanel {

    @Getter
    private String id;

    private String title;
    private String type;
    private Object content;

    public AbstractPanel() {
    }

    public AbstractPanel(String id, String title, String type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public abstract String display();


}