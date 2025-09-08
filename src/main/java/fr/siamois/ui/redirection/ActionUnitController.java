package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.NavBean;
import fr.siamois.ui.bean.panel.FlowBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class ActionUnitController {

    private final FlowBean flowBean;

    private static final String FLOW_FORWARD_PATH = "forward:/flow.xhtml";
    private final NavBean navBean;

    public ActionUnitController(FlowBean flowBean, NavBean navBean) {
        this.flowBean = flowBean;
        this.navBean = navBean;
    }

    @GetMapping("/actionunit/{id}")
    public String toActionUnit(@PathVariable Long id) {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        flowBean.addActionUnitPanel(id);
        return FLOW_FORWARD_PATH;
    }

    @GetMapping("/actionunit")
    public String toActionUnitList() {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        flowBean.addActionUnitListPanel(null);
        return FLOW_FORWARD_PATH;
    }

    @GetMapping("/spatial-unit/{id}/actionunit/new")
    public String addActionUnit(@PathVariable Long id) {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        // TODO : open new action unit dialog
        return FLOW_FORWARD_PATH;
    }

}
