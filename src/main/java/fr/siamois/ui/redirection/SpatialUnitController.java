package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.panel.FlowBean;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.annotation.SessionScope;



@Controller
@SessionScope
public class SpatialUnitController {

    private final FlowBean flowBean;

    public SpatialUnitController(FlowBean flowBean) {
        this.flowBean = flowBean;
    }

    @GetMapping("/spatial-unit/{id}")
    public String toSpatialUnit(@PathVariable Long id, Model model)  {
        flowBean.addSpatialUnitPanel(id);
        return "forward:/flow.xhtml";
    }

}
