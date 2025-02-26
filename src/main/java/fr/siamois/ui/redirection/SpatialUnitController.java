package fr.siamois.ui.redirection;

import fr.siamois.view.spatialunit.SpatialUnitBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;

@Controller
@SessionScoped
public class SpatialUnitController {

    private final SpatialUnitBean spatialUnitBean;

    public SpatialUnitController(SpatialUnitBean spatialUnitBean) {
        this.spatialUnitBean = spatialUnitBean;
    }

    @GetMapping("/spatialUnit/{id}")
    public String toSpatialUnit(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        spatialUnitBean.setId(id);
        return "forward:/pages/spatialUnit/spatialUnit.xhtml";
    }

}
