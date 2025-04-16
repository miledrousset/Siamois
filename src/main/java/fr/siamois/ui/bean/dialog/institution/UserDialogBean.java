package fr.siamois.ui.bean.dialog.institution;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.PendingPerson;
import fr.siamois.domain.services.person.PersonService;
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
    private Institution institution;
    private String title;
    private String buttonLabel;

    private String userEmail;

    public UserDialogBean(EmailManager emailManager, PersonService personService) {
        this.emailManager = emailManager;
        this.personService = personService;
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
        PendingPerson pendingPerson = new PendingPerson();
        pendingPerson.setEmail(userEmail);
        pendingPerson.setInstitution(institution);
        boolean isCreated = personService.createPendingManager(pendingPerson);
        if (!isCreated) {
            PrimeFaces.current().executeScript("PF('newManagerDialog').showError();");
        } else {
            log.trace("User created");
        }
    }

    public void exit() {
        reset();
        PrimeFaces.current().executeScript("PF('newManagerDialog').exit();");
    }

}
