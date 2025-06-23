package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.NavBean;
import fr.siamois.ui.bean.panel.FlowBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class WelcomeController {

    private final FlowBean flowBean;
    private final NavBean navBean;

    public WelcomeController(FlowBean flowBean, NavBean navBean) {
        this.flowBean = flowBean;
        this.navBean = navBean;
    }

    @GetMapping("/welcome")
    public String toWelcome() {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        flowBean.addWelcomePanel();
        return "forward:/flow.xhtml";
    }

}
