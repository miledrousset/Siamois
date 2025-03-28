package fr.siamois.ui.bean;

import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.io.Serializable;

@Slf4j
@Component
@RequestScope
public class RedirectBean implements Serializable {

    private final transient ServletContext servletContext;
    private final transient UserSettingsBean userSettingsBean;

    public RedirectBean(ServletContext servletContext, UserSettingsBean userSettingsBean) {
        this.servletContext = servletContext;
        this.userSettingsBean = userSettingsBean;
    }

    public String redirectUrl(String resource) {
        if (!resource.startsWith("/"))
            resource = "/" + resource;
        return servletContext.getContextPath() + resource;
    }

    public void redirectTo(String resource) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(redirectUrl(resource));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void redirectTo(HttpStatus errorStatus) {
        String errorCode = switch (errorStatus) {
            case NOT_FOUND -> "404";
            case FORBIDDEN -> "403";
            default -> "500";
        };

        redirectTo(String.format("/error/%s", errorCode));
    }

    public void goToUserSettings() throws NoConfigForFieldException {
        userSettingsBean.init();
        redirectTo("/user/config");
    }

}
