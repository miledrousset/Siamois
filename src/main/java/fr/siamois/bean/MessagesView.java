package fr.siamois.bean;

import fr.siamois.models.auth.Person;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;

@Getter
@SessionScoped
@Named
public class MessagesView implements Serializable {

    public void info() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Message Content"));
    }

    public void warn() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Message Content"));
    }

    public void error() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Message Content."));
    }

    public String greetUser() {
        AuthenticatedUserUtils utils = new AuthenticatedUserUtils();
        Optional<Person> opt = utils.getAuthenticatedUser();
        return opt.map(person -> "Hello " + person.getUsername() + " !").orElse("Hello ANONYMOUS ! You're not supposed to be here ...");
    }
}