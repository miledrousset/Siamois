package fr.siamois.ui.bean.dialog.institution;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.exceptions.institution.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.institution.InstitutionAlreadyExistException;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.SaveActionFromBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;

@Slf4j
@Component
@SessionScope
@Getter
@Setter
public class InstitutionCreationBean implements Serializable {

    private final transient InstitutionService institutionService;
    private final SessionSettingsBean sessionSettingsBean;
    private transient SaveActionFromBean saveActionFromBean;
    private String institutionName;
    private String identifier;
    private String description;

    public InstitutionCreationBean(InstitutionService institutionService, SessionSettingsBean sessionSettingsBean) {
        this.institutionService = institutionService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public void reset() {
        saveActionFromBean = null;
        institutionName = "";
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

    public void save() {
        assert saveActionFromBean != null;
        saveActionFromBean.save();
    }

    public void exit() {
        reset();
        PrimeFaces.current().executeScript("PF('institutionCreationDialog').hide();");
    }

}
