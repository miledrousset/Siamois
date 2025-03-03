package fr.siamois.ui.bean;

import fr.siamois.ui.bean.flow.EmptyDashboardBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@SessionScoped
@Getter
@Setter
public class FlowBean {

    private final List<FlowComponent> components = new ArrayList<>();
    private final EmptyDashboardBean emptyDashboardBean;

    public FlowBean(EmptyDashboardBean emptyDashboardBean) {
        this.emptyDashboardBean = emptyDashboardBean;
    }

    public void add(FlowComponent flowComponent) {
        components.add(0, flowComponent);
    }

    public void displayEmpty() {
        log.trace("Add asked");
        add(emptyDashboardBean);
    }

}
