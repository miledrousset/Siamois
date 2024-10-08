package fr.siamois.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import lombok.Getter;

import jakarta.faces.context.FacesContext;
import java.io.Serializable;

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

}