package fr.siamois.bean.Breadcrumb;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@Data
@SessionScoped
@Component
@Slf4j
public class BreadcrumbBean implements Serializable {
    private MenuModel model;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();

        // Home Item
        DefaultMenuItem item = DefaultMenuItem.builder()
                .value("Home")
                .outcome("/pages/home/home")
                .build();
        model.getElements().add(item);
    }

    public void addBreadcrumbItem(String label, String outcome, String icon) {
        log.error("Modifying BC");

        DefaultMenuItem item = DefaultMenuItem.builder()
                .value(label)
                .icon(icon)
                .outcome(outcome)
                .build();
        model.getElements().add(item);

    }
}
