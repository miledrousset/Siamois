package fr.siamois.ui.bean.flow;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.ui.bean.FlowBean;
import fr.siamois.ui.bean.FlowComponent;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.List;

@Slf4j
@Component
@SessionScoped
@Getter
@Setter
public class SpatialUnitListv2Bean implements FlowComponent {

    private final SpatialUnitService spatialUnitService;
    private final SessionSettingsBean sessionSettingsBean;
    private final FlowBean flowBean;
    private final SpatialUnitV2Bean spatialUnitV2Bean;
    private List<SpatialUnit> spatialUnits;

    public SpatialUnitListv2Bean(SpatialUnitService spatialUnitService, SessionSettingsBean sessionSettingsBean, FlowBean flowBean, SpatialUnitV2Bean spatialUnitV2Bean) {
        this.spatialUnitService = spatialUnitService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.flowBean = flowBean;
        this.spatialUnitV2Bean = spatialUnitV2Bean;
    }

    @Override
    public String display() {
        return "/pages/flow/spatialunitlistv2.xhtml";
    }

    public void displayChild(SpatialUnit spatialUnit) {
        log.trace("Display child");
        spatialUnitV2Bean.setSpatialUnit(spatialUnit);
        flowBean.add(spatialUnitV2Bean);
    }

    private void loadUnits() {
        Institution institution = sessionSettingsBean.getSelectedInstitution();
        spatialUnits = spatialUnitService.findAllOfInstitution(institution);
    }

    public List<SpatialUnit> getSpatialUnits() {
        if (spatialUnits == null) {
            loadUnits();
        }
        return spatialUnits;
    }
}
