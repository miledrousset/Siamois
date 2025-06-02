package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.panel.FlowBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class SpatialUnitController {

    public static final String FLOW_XHTML = "forward:/flow.xhtml";
    private final FlowBean flowBean;

    public SpatialUnitController(FlowBean flowBean) {
        this.flowBean = flowBean;
    }

    @GetMapping("/spatialunit/{id}")
    public String toSpatialUnit(@PathVariable Long id)  {
        flowBean.addSpatialUnitPanel(id);
        return FLOW_XHTML;
    }

    @GetMapping("/spatialunit")
    public String toSpatialUnitList() {
        flowBean.addSpatialUnitListPanel(null);
        return FLOW_XHTML;
    }

    @GetMapping("/spatialunit/add")
    public String toAddSpatialUnit()  {
        flowBean.addNewSpatialUnitPanel();
        return FLOW_XHTML;
    }

}
