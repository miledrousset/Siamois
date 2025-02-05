package fr.siamois.bean;

import fr.siamois.bean.converter.InstitutionConverter;
import fr.siamois.models.Institution;
import fr.siamois.models.auth.Person;
import fr.siamois.services.publisher.InstitutionChangeEventPublisher;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

    private final SessionSettings sessionSettings;
    private final InstitutionChangeEventPublisher institutionChangeEventPublisher;
    private final InstitutionConverter converter;

    private List<Institution> institutions;

    private Institution selectedInstitution;

    public NavBean(SessionSettings sessionSettings, InstitutionChangeEventPublisher institutionChangeEventPublisher, InstitutionConverter converter) {
        this.sessionSettings = sessionSettings;
        this.institutionChangeEventPublisher = institutionChangeEventPublisher;
        this.converter = converter;
    }

    public void init() {
        log.trace("Initializing NavBean");
        institutions = sessionSettings.getReferencedInstitutions();
        selectedInstitution = sessionSettings.getSelectedInstitution();
    }

    /**
     * Builds the logout path with the context path
     * @return the logout path
     */
    public String logoutPath() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        String contextPath = externalContext.getRequestContextPath();
        return contextPath + "/logout";
    }

    /**
     * Checks if the user is in the given role
     * @param roleName the role to check
     * @return true if the user is in the role, false otherwise
     */
    public boolean userIs(String roleName) {
        Optional<Person> optUser = AuthenticatedUserUtils.getAuthenticatedUser();
        return optUser.map(person -> person.hasRole(roleName)).orElse(false);
    }

    /**
     * Checks if the user is in any of the given roles
     * @param roles the roles to check
     * @return true if the user is in any of the roles, false otherwise
     */
    public boolean userIsAny(String... roles) {
        Optional<Person> optUser = AuthenticatedUserUtils.getAuthenticatedUser();
        if (optUser.isEmpty()) return false;

        Person user = optUser.get();
        for (String role : roles) {
            if (user.hasRole(role)) return true;
        }
        return false;
    }

    public void changeSelectedInstitution() {
        Institution oldInstit = sessionSettings.getSelectedInstitution();
        sessionSettings.setSelectedInstitution(selectedInstitution);
        institutionChangeEventPublisher.publishTeamChangeEvent();
        log.trace("Institution changed from {} to {}", oldInstit.toString(), selectedInstitution.toString());
    }
}
