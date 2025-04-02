package fr.siamois.ui.bean;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.events.InstitutionChangeEvent;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.models.settings.PersonSettings;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.utils.AuthenticatedUserUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

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
    private transient List<Institution> referencedInstitutions;
    private PersonSettings personSettings;

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

    public List<Institution> getReferencedInstitutions() {
        if (referencedInstitutions == null || referencedInstitutions.isEmpty()) {
            setupSession();
        }
        return referencedInstitutions;
    }

    public void setupSession() {
        personSettings = personService.createOrGetSettingsOf(getAuthenticatedUser());
        loadLanguageSettings();
        loadInstitutionsSettings();
    }

    private void loadLanguageSettings() {
        if (!StringUtils.isEmpty(personSettings.getLangCode())) {
            langBean.setLanguage(personSettings.getLangCode());
        }
    }

    @EventListener(InstitutionChangeEvent.class)
    private void loadInstitutionsSettings() {
        if (personSettings.getDefaultInstitution() != null) {
            selectedInstitution = personSettings.getDefaultInstitution();
            referencedInstitutions = findReferencedInstitutions();
        } else {
            setupInstitution();
        }
        assert selectedInstitution != null;
        institutionSettings = institutionService.createOrGetSettingsOf(selectedInstitution);
    }

    private List<Institution> findReferencedInstitutions() {
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
            redirectBean.redirectTo("/login");
        }
        return new UserInfo(selectedInstitution, getAuthenticatedUser(), getLanguageCode());
    }

    public InstitutionSettings getInstitutionSettings() {
        return getUserInfo().getInstitution().getSettings();
    }


    private void setupInstitution() {
        List<Institution> result = findReferencedInstitutions();

        result.sort(((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName())));

        referencedInstitutions = result;

        if (selectedInstitution == null && !result.isEmpty())
            selectedInstitution = result.get(0);
    }

}
