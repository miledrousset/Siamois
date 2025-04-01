package fr.siamois.ui.bean;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.utils.AuthenticatedUserUtils;
import lombok.Getter;
import lombok.Setter;
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
    private Institution selectedInstitution;
    private transient InstitutionSettings institutionSettings;
    private transient List<Institution> referencedInstitutions;

    public SessionSettingsBean(InstitutionService institutionService, LangBean langBean, RedirectBean redirectBean) {
        this.institutionService = institutionService;
        this.langBean = langBean;
        this.redirectBean = redirectBean;
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
        setupInstitution();
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
        Person authUser = getAuthenticatedUser();
        List<Institution> result;
        if (authUser.isSuperAdmin()) {
            result = institutionService.findAll();
        } else {
            result = institutionService.findInstitutionsOfPerson(authUser);
        }

        result.sort(((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName())));

        referencedInstitutions = result;

        if (selectedInstitution == null && !result.isEmpty())
            selectedInstitution = result.get(0);

        assert selectedInstitution != null;
        institutionSettings = institutionService.createOrGetSettingsOf(selectedInstitution);

    }

}
