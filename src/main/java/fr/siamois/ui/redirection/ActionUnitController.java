package fr.siamois.ui.redirection;

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

    public ActionUnitController(FlowBean flowBean) {
        this.flowBean = flowBean;
    }

    @GetMapping("/actionunit/{id}")
    public String toActionUnit(@PathVariable Long id) {
        flowBean.addActionUnitPanel(id);
        return FLOW_FORWARD_PATH;
    }

    @GetMapping("/actionunit")
    public String toActionUnitList() {
        flowBean.addActionUnitListPanel(null);
        return FLOW_FORWARD_PATH;
    }

    @GetMapping("/spatialunit/{id}/actionunit/new")
    public String addActionUnit(@PathVariable Long id) {
        flowBean.addNewActionUnitPanel(id);
        return FLOW_FORWARD_PATH;
    }

}
