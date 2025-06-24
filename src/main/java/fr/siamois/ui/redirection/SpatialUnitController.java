package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.NavBean;
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
    private final NavBean navBean;

    public SpatialUnitController(FlowBean flowBean, NavBean navBean) {
        this.flowBean = flowBean;
        this.navBean = navBean;
    }

    @GetMapping("/spatialunit/{id}")
    public String toSpatialUnit(@PathVariable Long id)  {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        flowBean.addSpatialUnitPanel(id);
        return FLOW_XHTML;
    }

    @GetMapping("/spatialunit")
    public String toSpatialUnitList() {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        flowBean.addSpatialUnitListPanel(null);
        return FLOW_XHTML;
    }

    @GetMapping("/spatialunit/add")
    public String toAddSpatialUnit()  {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        // todo : open new spatial unit dialog
        return FLOW_XHTML;
    }

}
