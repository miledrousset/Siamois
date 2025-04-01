package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.actionunit.ActionUnitBean;
import fr.siamois.ui.bean.panel.FlowBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class ActionUnitController {

    private final ActionUnitBean actionUnitBean;
    private final FlowBean flowBean;

    public ActionUnitController(ActionUnitBean actionUnitBean, FlowBean flowBean) {
        this.actionUnitBean = actionUnitBean;
        this.flowBean = flowBean;
    }

    @GetMapping("/action-unit/{id}")
    public String toActionUnit(@PathVariable Long id, Model model) {
        flowBean.addActionUnitPanel(id);
        return "forward:/flow.xhtml";
    }

}
