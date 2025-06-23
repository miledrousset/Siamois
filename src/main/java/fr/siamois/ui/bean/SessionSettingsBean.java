package fr.siamois.ui.bean;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.events.InstitutionChangeEvent;
import fr.siamois.domain.models.events.LangageChangeEvent;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.models.settings.PersonSettings;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.utils.AuthenticatedUserUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Component
@SessionScoped
public class SessionSettingsBean implements Serializable {

    private final transient InstitutionService institutionService;
    private final LangBean langBean;
    private final transient RedirectBean redirectBean;
    private final transient PersonService personService;
    private Institution selectedInstitution;
    private InstitutionSettings institutionSettings;
    private PersonSettings personSettings;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private UserInfo userInfo;

    public SessionSettingsBean(InstitutionService institutionService,
                               LangBean langBean,
                               RedirectBean redirectBean,
                               PersonService personService) {
        this.institutionService = institutionService;
        this.langBean = langBean;
        this.redirectBean = redirectBean;
        this.personService = personService;
    }

    public Person getAuthenticatedUser() {
        return AuthenticatedUserUtils.getAuthenticatedUser().orElse(null);
    }

    public Institution getSelectedInstitution() {
        return getUserInfo().getInstitution();
    }

    public void setupSession() {
        personSettings = personService.createOrGetSettingsOf(getAuthenticatedUser());
        loadLanguageSettings();
        loadInstitutionsSettings();
        userInfo = null;
    }

    private void loadLanguageSettings() {
        if (!StringUtils.isEmpty(personSettings.getLangCode())) {
            langBean.setLanguage(personSettings.getLangCode());
        }
    }


    private void loadInstitutionsSettings() {
        if (personSettings.getDefaultInstitution() != null) {
            selectedInstitution = personSettings.getDefaultInstitution();
        } else {
            Set<Institution> allInstitutions = findReferencedInstitutions();
            selectedInstitution = allInstitutions.stream().findFirst().orElse(null);
        }
        assert selectedInstitution != null;
        institutionSettings = institutionService.createOrGetSettingsOf(selectedInstitution);
    }

    private Set<Institution> findReferencedInstitutions() {
        Person person = getAuthenticatedUser();
        if (person.isSuperAdmin()) {
            return institutionService.findAll();
        } else {
            return institutionService.findInstitutionsOfPerson(person);
        }
    }

    public String getLanguageCode() {
        return langBean.getLanguageCode();
    }

    public UserInfo getUserInfo() {
        if (selectedInstitution == null || getAuthenticatedUser() == null) {
            return null;
        }
        if (userInfo == null) {
            userInfo = new UserInfo(selectedInstitution, getAuthenticatedUser(), getLanguageCode());
        }
        return userInfo;
    }

    @EventListener({InstitutionChangeEvent.class, LangageChangeEvent.class})
    public void markUserInfoAsChanged() {
        userInfo = null;
    }

    public List<Person> completePerson(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        query = query.toLowerCase();
        return personService.findAllByNameLastnameContaining(query);
    }

}
