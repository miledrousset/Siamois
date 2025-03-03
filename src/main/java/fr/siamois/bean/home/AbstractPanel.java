package fr.siamois.bean.home;

import lombok.Data;

@Data
public class AbstractPanel {

    private String id;
    private String title;
    private String type;
    private Object content;

    public AbstractPanel() {}

    public AbstractPanel(String id, String title, String type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }
}
