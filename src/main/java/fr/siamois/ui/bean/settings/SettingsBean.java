package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@Getter
@Setter
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class SettingsBean {

    private final SessionSettingsBean sessionSettingsBean;
    private final PersonService personService;
    private String fEmail;
    private String fLastname;
    private String fFirstname;

    public SettingsBean(SessionSettingsBean sessionSettingsBean, PersonService personService) {
        this.sessionSettingsBean = sessionSettingsBean;
        this.personService = personService;
    }

    public void init() {
        UserInfo info = sessionSettingsBean.getUserInfo();
        Person user = info.getUser();
        fEmail = user.getMail();
        fLastname = user.getLastname();
        fFirstname = user.getName();
    }

    public void saveProfile() {
        Person user = sessionSettingsBean.getAuthenticatedUser();
        if (fEmail != null && !fEmail.isEmpty() && !fEmail.equals(user.getMail())) {
            user.setMail(fEmail);
        }

        if (fLastname != null && !fLastname.isEmpty() && !fLastname.equals(user.getLastname())) {
            user.setLastname(fLastname);
        }

        if (fFirstname != null && !fFirstname.isEmpty() && !fFirstname.equals(user.getName())) {
            user.setName(fFirstname);
        }

        try {
            personService.updatePerson(user);
        } catch (InvalidNameException e) {
            log.error(e.getMessage());
        } catch (InvalidUserInformationException | UserAlreadyExistException e) {
            log.error("There was a problem while updating the person", e);
        }
    }

}
