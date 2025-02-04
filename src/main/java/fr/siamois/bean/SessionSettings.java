package fr.siamois.bean;

import fr.siamois.models.Institution;
import fr.siamois.models.UserInfo;
import fr.siamois.models.auth.Person;
import fr.siamois.services.InstitutionService;
import fr.siamois.utils.AuthenticatedUserUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.List;
import java.util.Locale;

@Setter
@Getter
@Component
@SessionScoped
public class SessionSettings {

    private final InstitutionService institutionService;
    private final LangBean langBean;
    private Institution selectedInstitution;
    private List<Institution> referencedInstitutions;

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
        if (referencedInstitutions == null) {
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

        if (!result.isEmpty())
            selectedInstitution = result.get(0);

        referencedInstitutions = result;
    }

}
