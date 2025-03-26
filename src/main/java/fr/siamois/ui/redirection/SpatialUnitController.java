package fr.siamois.ui.redirection;

import fr.siamois.ui.bean.spatialunit.SpatialUnitBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;

@Controller
@SessionScope
public class SpatialUnitController {


    private final SpatialUnitBean spatialUnitBean;

    public SpatialUnitController(SpatialUnitBean spatialUnitBean) {
        this.spatialUnitBean = spatialUnitBean;
    }

    @GetMapping("/spatialunit/{id}")
    public String toSpatialUnit(@PathVariable Long id, Model model) throws IOException {
        model.addAttribute("id", id);
        spatialUnitBean.setId(id);
        spatialUnitBean.init();
        return "forward:/pages/spatialUnit/spatialUnit.xhtml";
    }

}
