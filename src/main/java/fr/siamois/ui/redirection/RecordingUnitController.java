package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.recordingunit.NewRecordingUnitFormBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class RecordingUnitController {

    private final NewRecordingUnitFormBean newRecordingUnitFormBean;

    public RecordingUnitController(NewRecordingUnitFormBean newRecordingUnitFormBean) {
        this.newRecordingUnitFormBean = newRecordingUnitFormBean;
    }

    @GetMapping("/recordingunit/{id}")
    public String toRecordingUnit(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        newRecordingUnitFormBean.setId(id);
        return "forward:/pages/recordingUnit/recordingUnit.xhtml";
    }

}
