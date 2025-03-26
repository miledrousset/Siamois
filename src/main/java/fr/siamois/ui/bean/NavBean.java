package fr.siamois.ui.bean;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.publisher.InstitutionChangeEventPublisher;
import fr.siamois.domain.utils.AuthenticatedUserUtils;
import fr.siamois.ui.bean.converter.InstitutionConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;


/**
 * Bean to manage the navigation bar of the application. Allows the user to select a team.
 *
 * @author Julien Linget
 */
@Slf4j
@Component
@Getter
@Setter
@SessionScoped
public class NavBean implements Serializable {

    private final SessionSettingsBean sessionSettingsBean;
    private final transient InstitutionChangeEventPublisher institutionChangeEventPublisher;
    private final transient InstitutionConverter converter;
    private final transient InstitutionService institutionService;

    private transient List<Institution> institutions;

    public NavBean(SessionSettingsBean sessionSettingsBean,
                   InstitutionChangeEventPublisher institutionChangeEventPublisher,
                   InstitutionConverter converter, InstitutionService institutionService) {
        this.sessionSettingsBean = sessionSettingsBean;
        this.institutionChangeEventPublisher = institutionChangeEventPublisher;
        this.converter = converter;
        this.institutionService = institutionService;
    }

    public void init() {
        log.trace("Initializing NavBean");
        institutions = sessionSettingsBean.getReferencedInstitutions();
    }

    /**
     * Checks if the user is in the given role
     * @return true if the user is in the role, false otherwise
     */
    public boolean userIsAdmin() {
        Optional<Person> optUser = AuthenticatedUserUtils.getAuthenticatedUser();
        return optUser.map(Person::isSuperAdmin).orElse(false);
    }

    public void changeSelectedInstitution(Institution institution) {
        Institution old = sessionSettingsBean.getSelectedInstitution();
        sessionSettingsBean.setSelectedInstitution(institution);
        institutionChangeEventPublisher.publishInstitutionChangeEvent();
        log.trace("Institution changed from {} to {}", old.getName(), institution.getName());
    }

    public boolean institutionRefIsEmpty() {
        if (institutions == null || institutions.isEmpty()) {
            institutions = sessionSettingsBean.getReferencedInstitutions();
        }
        return institutions.isEmpty();
    }

    public Institution getSelectedInstitution() {
        return sessionSettingsBean.getSelectedInstitution();
    }

    public boolean isManagerOrAdminOfInstitution() {
        Optional<Person> optUser = AuthenticatedUserUtils.getAuthenticatedUser();
        if (optUser.isEmpty())
            return false;
        Person person = optUser.get();
        Institution institution = sessionSettingsBean.getSelectedInstitution();
        return userIsAdmin() || institutionService.isManagerOf(institution, person);
    }

    public void updateInstitutions() {
        sessionSettingsBean.setupSession();
        PrimeFaces.current().ajax().update("institutionForm:institutionSelector");
    }

    public Person currentUser() {
        return sessionSettingsBean.getAuthenticatedUser();
    }

}
