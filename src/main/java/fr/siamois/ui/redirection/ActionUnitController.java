package fr.siamois.ui.redirection;

import fr.siamois.view.actionunit.ActionUnitBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class ActionUnitController {

    private final ActionUnitBean actionUnitBean;

    public ActionUnitController(ActionUnitBean actionUnitBean) {
        this.actionUnitBean = actionUnitBean;
    }

    @GetMapping("/actionunit/{id}")
    public String toActionUnit(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        actionUnitBean.setId(id);
        return "forward:/pages/actionUnit/actionUnit.xhtml";
    }

}
