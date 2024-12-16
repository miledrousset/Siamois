package fr.siamois.utils;

import fr.siamois.bean.LangBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public class MessageUtils {

    public static void displayMessage(LangBean langBean, FacesMessage.Severity severity, String message) {
        String titleCode = null;

        if (severity.equals(FacesMessage.SEVERITY_INFO)) {
            titleCode = "commons.message.state.info";
        } else if (severity.equals(FacesMessage.SEVERITY_WARN)) {
            titleCode = "commons.message.state.warning";
        } else if (severity.equals(FacesMessage.SEVERITY_ERROR)) {
            titleCode = "commons.message.state.error";
        } else if (severity.equals(FacesMessage.SEVERITY_FATAL)) {
            titleCode = "commons.message.state.fatal";
        }

        String title = message;
        if (titleCode != null) {
            title = langBean.msg(titleCode);
        }

        displayMessage(severity, title, message);
    }

    public static void displayMessage(FacesMessage.Severity severity, String title, String message) {
        FacesMessage facesMessage = new FacesMessage(severity, title, message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }


    public static void displayInfoMessage(LangBean langBean, String message) {
        displayMessage(langBean, FacesMessage.SEVERITY_INFO, message);
    }

    public static void displayErrorMessage(LangBean langBean, String msg) {
        displayMessage(langBean, FacesMessage.SEVERITY_ERROR, msg);
    }
}
