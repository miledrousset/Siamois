package fr.siamois.ui.bean;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.faces.context.FacesContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

@Slf4j
@Component
@SessionScoped
public class PresentationBean implements Serializable {

    private final SessionSettingsBean sessionSettingsBean;

    public PresentationBean(SessionSettingsBean sessionSettingsBean) {
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public void continueToDashboardIfLogged() {
        Optional<Person> opt = AuthenticatedUserUtils.getAuthenticatedUser();
        UserInfo userInfo = sessionSettingsBean.getUserInfo();
        try {
            if (opt.isEmpty() || userInfo == null || userInfo.getInstitution() == null) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("login");
            } else {
                FacesContext.getCurrentInstance().getExternalContext().redirect("dashboard");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void continueIfLogged() {
        Optional<Person> opt = AuthenticatedUserUtils.getAuthenticatedUser();
        UserInfo userInfo = sessionSettingsBean.getUserInfo();
        try {
            if (opt.isEmpty() || userInfo == null || userInfo.getInstitution() == null) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("login");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
