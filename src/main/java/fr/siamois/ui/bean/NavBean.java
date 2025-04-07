package fr.siamois.ui.bean;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.publisher.InstitutionChangeEventPublisher;
import fr.siamois.ui.bean.converter.InstitutionConverter;
import fr.siamois.ui.bean.settings.InstitutionListSettingsBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;


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
    private final RedirectBean redirectBean;
    private final InstitutionListSettingsBean institutionListSettingsBean;

    private ApplicationMode applicationMode = ApplicationMode.SIAMOIS;

    public NavBean(SessionSettingsBean sessionSettingsBean,
                   InstitutionChangeEventPublisher institutionChangeEventPublisher,
                   InstitutionConverter converter, InstitutionService institutionService, RedirectBean redirectBean, InstitutionListSettingsBean institutionListSettingsBean) {
        this.sessionSettingsBean = sessionSettingsBean;
        this.institutionChangeEventPublisher = institutionChangeEventPublisher;
        this.converter = converter;
        this.institutionService = institutionService;
        this.redirectBean = redirectBean;
        this.institutionListSettingsBean = institutionListSettingsBean;
    }


    public boolean userIsSuperAdmin() {
        return sessionSettingsBean.getUserInfo().getUser().isSuperAdmin();
    }

    public Institution getSelectedInstitution() {
        return sessionSettingsBean.getSelectedInstitution();
    }

    public void updateInstitutions() {
        sessionSettingsBean.setupSession();
        PrimeFaces.current().ajax().update("institutionForm:institutionSelector");
    }

    public Person currentUser() {
        return sessionSettingsBean.getAuthenticatedUser();
    }

    public boolean isSiamoisMode() {
        return applicationMode == ApplicationMode.SIAMOIS;
    }

    public boolean isSettingsMode() {
        return applicationMode == ApplicationMode.SETTINGS;
    }

    public void goToInstitutionManager() {
        institutionListSettingsBean.init();
        redirectBean.redirectTo("/settings/organisation");
    }

    public enum ApplicationMode {
        SIAMOIS,
        SETTINGS
    }

}
