package fr.siamois.ui.bean.dialog.institution;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.ui.bean.ActionFromBean;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.email.EmailManager;
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
public class UserDialogBean implements Serializable {

    private final transient EmailManager emailManager;
    private final transient PersonService personService;
    private final transient InstitutionService institutionService;
    private final LangBean langBean;

    private transient ActionFromBean actionFromBean;

    private Institution institution;
    private String title;
    private String buttonLabel;

    private String userEmail;
    private String roleName;

    private boolean shouldRenderRoleField = false;

    public UserDialogBean(EmailManager emailManager, PersonService personService, InstitutionService institutionService, LangBean langBean) {
        this.emailManager = emailManager;
        this.personService = personService;
        this.institutionService = institutionService;
        this.langBean = langBean;
    }

    public void init(String title, String buttonLabel, Institution institution, ActionFromBean actionFromBean) {
        reset();
        this.title = title;
        this.buttonLabel = buttonLabel;
        this.institution = institution;
        this.shouldRenderRoleField = false;
        this.actionFromBean = actionFromBean;
    }

    public void init(String title, String buttonLabel, Institution institution, boolean shouldRenderRole, ActionFromBean actionFromBean) {
        reset();
        this.title = title;
        this.buttonLabel = buttonLabel;
        this.institution = institution;
        this.shouldRenderRoleField = shouldRenderRole;
        this.actionFromBean = actionFromBean;
    }

    public void reset() {
        this.institution = null;
        this.title = null;
        this.buttonLabel = null;
        this.userEmail = null;
        this.roleName = null;
        this.shouldRenderRoleField = false;
        this.actionFromBean = null;
    }

    public void exit() {
        reset();
        PrimeFaces.current().executeScript("PF('newMemberDialog').exit();");
    }

}
