package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.NavBean;
import fr.siamois.ui.bean.panel.FlowBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class RecordingUnitController {

    private final FlowBean flowBean;
    private final NavBean navBean;

    public RecordingUnitController(FlowBean flowBean, NavBean navBean) {
        this.flowBean = flowBean;
        this.navBean = navBean;
    }

    @GetMapping("/recordingunit/{id}")
    public String toRecordingUnit(@PathVariable Long id) {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        flowBean.addRecordingUnitPanel(id);
        return "forward:/flow.xhtml";
    }

    @GetMapping("/actionunit/{id}/recordingunit/new")
    public String newRecordingUnit(@PathVariable Long id) {
        navBean.setApplicationMode(NavBean.ApplicationMode.SIAMOIS);
        flowBean.addNewRecordingUnitPanel(id, 0);
        return "forward:/flow.xhtml";
    }

}
