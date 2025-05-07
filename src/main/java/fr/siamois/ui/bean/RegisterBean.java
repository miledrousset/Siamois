package fr.siamois.ui.bean;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.pending.PendingPerson;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.person.PersonService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;

@Slf4j
@Component
@SessionScoped
@Getter
@Setter
public class RegisterBean {

    private final PersonService personService;
    private final InstitutionService institutionService;
    private final LangBean langBean;
    private final RedirectBean redirectBean;
    private String email;
    private String password;
    private String confirmPassword;
    private Institution institution;
    private String firstName;
    private String lastName;
    private String username;

    public RegisterBean(PersonService personService, InstitutionService institutionService, LangBean langBean, RedirectBean redirectBean) {
        this.personService = personService;
        this.institutionService = institutionService;
        this.langBean = langBean;
        this.redirectBean = redirectBean;
    }

    public void reset() {
        email = null;
        password = null;
        confirmPassword = null;
        institution = null;
        firstName = null;
        lastName = null;
        username = null;
    }

    public void init(PendingPerson pendingPerson) {
        reset();
        this.email = pendingPerson.getEmail();
        // this.institution = pendingPerson.getInstitution(); TODO: Fix this
    }

    public void register() {

        if (email == null || password == null || confirmPassword == null) {
            log.trace("Email and password are not set");
            return;
        }

        if (!password.equals(confirmPassword)) {
            log.trace("Password and confirm password are not the same");
            return;
        }

        Person person = new Person();
        person.setEmail(email);
        person.setName(firstName);
        person.setLastname(lastName);
        person.setPassword(password);
        person.setUsername(username);

        try {
            person = personService.createPerson(person);
            log.trace("Person created");

            institutionService.addToManagers(institution, person);
            log.trace("Person added as manager to institution");

            redirectBean.redirectTo("/login");

        } catch (InvalidUserInformationException e) {
            log.trace("Person could not be created");
        } catch (UserAlreadyExistException e) {
            log.trace("User already exists");
        }

    }

}
