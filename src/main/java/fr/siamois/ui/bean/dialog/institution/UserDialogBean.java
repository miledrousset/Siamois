package fr.siamois.ui.bean.dialog.institution;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.PendingPerson;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.email.EmailManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Optional;

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
    private Institution institution;
    private String title;
    private String buttonLabel;

    private String userEmail;

    public UserDialogBean(EmailManager emailManager, PersonService personService, InstitutionService institutionService, LangBean langBean) {
        this.emailManager = emailManager;
        this.personService = personService;
        this.institutionService = institutionService;
        this.langBean = langBean;
    }

    public void init(String title, String buttonLabel, Institution institution) {
        reset();
        this.title = title;
        this.buttonLabel = buttonLabel;
        this.institution = institution;
    }

    public void reset() {
        log.trace("Reset users");
    }

    public void save() {
        Optional<Person> existingsUser = personService.findByEmail(userEmail);
        if (existingsUser.isPresent()) {
            boolean isAdded = institutionService.addToManagers(institution, existingsUser.get());
            if (!isAdded) {
                MessageUtils.displayWarnMessage(langBean, "organisationSettings.error.manager", existingsUser.get().getMail(), institution.getName());
                PrimeFaces.current().executeScript("PF('newManagerDialog').showError();");
                return;
            }
            MessageUtils.displayInfoMessage(langBean, "organisationSettings.action.addUserToManager", existingsUser.get().getMail());
            PrimeFaces.current().executeScript("PF('newManagerDialog').exit();");
            return;
        }

        PendingPerson pendingPerson = new PendingPerson();
        pendingPerson.setEmail(userEmail);
        pendingPerson.setInstitution(institution);
        boolean isCreated = personService.createPendingManager(pendingPerson);
        if (!isCreated) {
            MessageUtils.displayErrorMessage(langBean, "common.error.internal");
            PrimeFaces.current().executeScript("PF('newManagerDialog').showError();");
        } else {
            MessageUtils.displayInfoMessage(langBean, "organisationSettings.action.sendInvite", userEmail);
        }
    }

    public void exit() {
        reset();
        PrimeFaces.current().executeScript("PF('newManagerDialog').exit();");
    }

}
