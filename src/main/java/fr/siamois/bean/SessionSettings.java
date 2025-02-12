package fr.siamois.bean;

import fr.siamois.models.Institution;
import fr.siamois.models.UserInfo;
import fr.siamois.models.auth.Person;
import fr.siamois.services.InstitutionService;
import fr.siamois.utils.AuthenticatedUserUtils;
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
public class SessionSettings implements Serializable {

    private final transient InstitutionService institutionService;
    private final LangBean langBean;
    private Institution selectedInstitution;
    private transient List<Institution> referencedInstitutions;

    public SessionSettings(InstitutionService institutionService, LangBean langBean) {
        this.institutionService = institutionService;
        this.langBean = langBean;
    }

    public Person getAuthenticatedUser() {
        return AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow(() -> new IllegalStateException("No authenticated user but user should be authenticated"));
    }

    public Institution getSelectedInstitution() {
        if (selectedInstitution == null) {
            setupSession();
        }
        return selectedInstitution;
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
        return new UserInfo(selectedInstitution, getAuthenticatedUser(), getLanguageCode());
    }

    private void setupInstitution() {
        Person authUser = getAuthenticatedUser();
        List<Institution> result;
        if (authUser.hasRole("ADMIN")) {
            result = institutionService.findAll();
        } else {
            result = institutionService.findInstitutionsOfPerson(authUser);
        }

        result.sort(((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName())));

        referencedInstitutions = result;

        if (selectedInstitution == null && !result.isEmpty())
            selectedInstitution = result.get(0);

    }

}
