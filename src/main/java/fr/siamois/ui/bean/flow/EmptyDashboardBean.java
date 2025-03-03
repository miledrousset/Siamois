package fr.siamois.ui.bean.flow;

import fr.siamois.ui.bean.FlowComponent;
import org.springframework.stereotype.Component;

import javax.faces.bean.ViewScoped;

@Component
@ViewScoped
public class EmptyDashboardBean implements FlowComponent {
    @Override
    public String display() {
        return "/pages/flow/empty.xhtml";
    }
}
