package fr.siamois.ui.bean.flow;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.ui.bean.FlowComponent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;

@Component
@SessionScoped
@Getter
@Setter
public class SpatialUnitV2Bean implements FlowComponent {

    private SpatialUnit spatialUnit;

    @Override
    public String display() {
        return "/pages/flow/spatialunitv2.xhtml";
    }
}
