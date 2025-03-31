package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.NavBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class SettingsController {

    private final NavBean navBean;

    public SettingsController(NavBean navBean) {
        this.navBean = navBean;
    }

    @GetMapping("/settings")
    public String goToSettings() {
        navBean.setApplicationMode(NavBean.ApplicationMode.SETTINGS);
        return "forward:/pages/settings/settings.xhtml";
    }

    @GetMapping("/dashboard")
    public String goToDashboard() {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        return "forward:/flow.xhtml";
    }

}
