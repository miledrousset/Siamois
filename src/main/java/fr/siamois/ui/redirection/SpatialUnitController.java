package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.NavBean;
import fr.siamois.ui.bean.dialog.spatialunit.NewSpatialUnitDialogBean;
import fr.siamois.ui.bean.panel.FlowBean;
import org.primefaces.PrimeFaces;
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
    private final NewSpatialUnitDialogBean newSpatialUnitDialogBean;

    public SpatialUnitController(FlowBean flowBean, NavBean navBean, NewSpatialUnitDialogBean newSpatialUnitDialogBean) {
        this.flowBean = flowBean;
        this.navBean = navBean;
        this.newSpatialUnitDialogBean = newSpatialUnitDialogBean;
    }

    @GetMapping("/spatial-unit/{id}")
    public String toSpatialUnit(@PathVariable Long id)  {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        flowBean.addSpatialUnitPanel(id);
        return FLOW_XHTML;
    }

    @GetMapping("/spatial-unit")
    public String toSpatialUnitList() {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        flowBean.addSpatialUnitListPanel(null);
        return FLOW_XHTML;
    }

    @GetMapping("/spatial-unit/add")
    public String toAddSpatialUnit()  {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        newSpatialUnitDialogBean.init();
        // Show dialog
        PrimeFaces.current().executeScript("PF('newSpatialUnitDialog').show();");
        return FLOW_XHTML;
    }

}
