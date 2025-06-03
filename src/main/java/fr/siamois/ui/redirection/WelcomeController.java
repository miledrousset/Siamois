package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.panel.FlowBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class WelcomeController {

    private final FlowBean flowBean;

    public WelcomeController(FlowBean flowBean) {
        this.flowBean = flowBean;
    }

    @GetMapping("/welcome")
    public String toWelcome() {
        flowBean.addWelcomePanel();
        return "forward:/flow.xhtml";
    }

}
