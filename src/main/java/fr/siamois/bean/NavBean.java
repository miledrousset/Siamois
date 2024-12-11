package fr.siamois.bean;

import fr.siamois.models.auth.Person;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Optional;

@Component
@SessionScoped
public class NavBean implements Serializable {

    public String logoutPath() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        String contextPath = externalContext.getRequestContextPath();
        return contextPath + "/logout";
    }

    public boolean userIs(String roleName) {
        Optional<Person> user = AuthenticatedUserUtils.getAuthenticatedUser();
        return user.map(person -> person.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase(roleName)))
                .orElse(false);
    }

    public boolean userIsAny(String... roles) {
        Optional<Person> user = AuthenticatedUserUtils.getAuthenticatedUser();
        return user.map(person -> person.getAuthorities()
                .stream()
                .anyMatch(a -> {
                    for (String role : roles) {
                        if (a.getAuthority().equalsIgnoreCase(role)) {
                            return true;
                        }
                    }
                    return false;
                }))
                .orElse(false);
    }
}
