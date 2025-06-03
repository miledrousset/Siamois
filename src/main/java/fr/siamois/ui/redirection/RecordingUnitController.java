package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.panel.FlowBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class RecordingUnitController {


    private final FlowBean flowBean;

    public RecordingUnitController(FlowBean flowBean) {
        this.flowBean = flowBean;
    }

    @GetMapping("/recordingunit/{id}")
    public String toRecordingUnit(@PathVariable Long id, Model model) {
        flowBean.addRecordingUnitPanel(id);
        return "forward:/flow.xhtml";
    }

    @GetMapping("/actionunit/{id}/recordingunit/new")
    public String newRecordingUnit(@PathVariable Long id) {
        flowBean.addNewRecordingUnitPanel(id, 0);
        return "forward:/flow.xhtml";
    }

}
