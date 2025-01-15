package fr.siamois.bean.ActionUnit;

import fr.siamois.models.ActionUnit;
import fr.siamois.services.ActionUnitService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;


@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class ActionUnitBean implements Serializable {

    // Deps
    private final ActionUnitService actionUnitService;

    // Local
    private ActionUnit actionUnit;
    private String actionUnitErrorMessage;
    private Long id;  // ID of the action unit requested

    public ActionUnitBean(ActionUnitService actionUnitService) {
        this.actionUnitService = actionUnitService;
    }

    public void init() {

        // reinit
        actionUnitErrorMessage = null;
        actionUnit = null;

       // Get the request action from DB
        try {
            if(id!=null) {
                actionUnit = actionUnitService.findById(id);
            }
            else {
                this.actionUnitErrorMessage = "No action unit ID specified";
            }
        }
        catch (RuntimeException e) {
            this.actionUnitErrorMessage = "Failed to load action unit: " + e.getMessage();
        }

    }


}
