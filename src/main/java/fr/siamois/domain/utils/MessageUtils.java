package fr.siamois.domain.utils;

import fr.siamois.ui.bean.LangBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.util.HashMap;
import java.util.Map;

public class MessageUtils {

    private static final Map<FacesMessage.Severity, String> titlesCodes = new HashMap<>();

    private MessageUtils() {}

    static {
        titlesCodes.put(FacesMessage.SEVERITY_INFO, "commons.message.state.info");
        titlesCodes.put(FacesMessage.SEVERITY_ERROR, "commons.message.state.error");
    }

    public static void displayMessage(LangBean langBean, FacesMessage.Severity severity, String messageCode, Object... args) {
        String title = messageCode;
        if (titlesCodes.containsKey(severity))
            title = langBean.msg(titlesCodes.get(severity));

        displayMessage(severity, title, langBean.msg(messageCode, args));

        if (!titlesCodes.containsKey(severity))
            throw new IllegalArgumentException("Unknown severity: " + severity + ". Replaced by messageCode");
    }

    public static void displayPlainMessage(LangBean langBean, FacesMessage.Severity severity, String plainMessage, Object... args) {
        String title = plainMessage;
        if (titlesCodes.containsKey(severity))
            title = langBean.msg(titlesCodes.get(severity));

        displayMessage(severity, title, String.format(plainMessage, args));

    }

    public static void displayMessage(FacesMessage.Severity severity, String title, String msgCode) {
        FacesMessage facesMessage = new FacesMessage(severity, title, msgCode);
        FacesContext.getCurrentInstance().addMessage("templateForm:templateGrowl", facesMessage);
    }

    public static void displayInfoMessage(LangBean langBean, String msgCode, Object... args) {
        displayMessage(langBean, FacesMessage.SEVERITY_INFO, msgCode, args);
    }

    public static void displayErrorMessage(LangBean langBean, String msgCode, Object... args) {
        displayMessage(langBean, FacesMessage.SEVERITY_ERROR, msgCode, args);
    }
}
