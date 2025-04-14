package fr.siamois.ui.bean.dialog.institution;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.exceptions.institution.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.institution.InstitutionAlreadyExistException;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.ActionFromBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@Slf4j
@Component
@SessionScoped
@Getter
@Setter
public class InstitutionDialogBean implements Serializable {

    private String title = "Créer une organisation";
    private String buttonLabel = "Créer l'organisation";

    private final transient InstitutionService institutionService;
    private final SessionSettingsBean sessionSettingsBean;
    private transient ActionFromBean actionFromBean;
    private String institutionName;
    private String identifier;
    private String description;

    public InstitutionDialogBean(InstitutionService institutionService, SessionSettingsBean sessionSettingsBean) {
        this.institutionService = institutionService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public void reset() {
        log.trace("Reset called");
        actionFromBean = null;
        institutionName = "";
        identifier = "";
        description = "";
        title = "";
        buttonLabel = "";
    }

    public Institution createInstitution() throws InstitutionAlreadyExistException, FailedInstitutionSaveException {
        Institution institution = new Institution();
        institution.setName(institutionName);
        institution.setIdentifier(identifier);
        institution.setId(-1L);
        institution.setManager(sessionSettingsBean.getAuthenticatedUser());
        institution.setDescription(description);
        return institutionService.createInstitution(institution);
    }

    public Institution updateInstitution(Institution institution) {
        if (institutionName != null && !institution.getName().isBlank()  && !institutionName.equals(institution.getName())) {
            institution.setName(institutionName);
        }
        if (identifier != null && !identifier.isBlank() && !identifier.equals(institution.getIdentifier())) {
            institution.setIdentifier(identifier);
        }
        if (description != null && !description.isBlank() && !description.equals(institution.getDescription())) {
            institution.setDescription(description);
        }
        return institutionService.update(institution);
    }

    public void save() {
        assert actionFromBean != null;
        actionFromBean.apply();
    }

    public void exit() {
        log.trace("Exit organization dialog");
        reset();
        PrimeFaces.current().executeScript("PF('newInstitutionDialog').close();");
    }

}
