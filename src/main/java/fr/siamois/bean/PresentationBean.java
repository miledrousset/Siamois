package fr.siamois.bean;

import fr.siamois.models.auth.Person;
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

    public void checkAuth() {
        log.trace("CheckAuth called");
        Optional<Person> opt = AuthenticatedUserUtils.getAuthenticatedUser();
        if (opt.isPresent()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("dashboard");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
