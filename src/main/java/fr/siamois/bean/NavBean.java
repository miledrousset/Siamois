package fr.siamois.bean;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@Component
@SessionScoped
public class NavBean implements Serializable {

    public String logoutPath() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        String contextPath = externalContext.getRequestContextPath();
        return contextPath + "/logout";
    }

}
