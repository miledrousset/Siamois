package fr.siamois.utils;

import fr.siamois.ui.bean.LangBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;

import java.util.HashMap;
import java.util.Map;

public class MessageUtils {

    private static final Map<FacesMessage.Severity, String> titlesCodes = new HashMap<>();

    private MessageUtils() {}

    static {
        titlesCodes.put(FacesMessage.SEVERITY_INFO, "common.message.state.info");
        titlesCodes.put(FacesMessage.SEVERITY_ERROR, "common.message.state.error");
        titlesCodes.put(FacesMessage.SEVERITY_WARN, "common.message.state.warn");
    }

    public static void displayMessage(LangBean langBean, FacesMessage.Severity severity, String messageCode, Object... args) {
        String title = messageCode;
        if (titlesCodes.containsKey(severity))
            title = langBean.msg(titlesCodes.get(severity));

        displayMessage(severity, title, langBean.msg(messageCode, args));

        if (!titlesCodes.containsKey(severity))
            throw new IllegalArgumentException("Unknown severity: " + severity + ". Replaced by messageCode");
    }

    public static void displayMessage(FacesMessage.Severity severity, String title, String msgContent) {
        FacesMessage facesMessage = new FacesMessage(severity, title, msgContent);
        FacesContext.getCurrentInstance().addMessage("templateFormCC:templateForm:templateGrowl", facesMessage);
        PrimeFaces.current().ajax().update("templateFormCC:templateForm:templateGrowl");
    }

    public static void displayInfoMessage(LangBean langBean, String msgCode, Object... args) {
        displayMessage(langBean, FacesMessage.SEVERITY_INFO, msgCode, args);
    }


    public static void displayErrorMessage(LangBean langBean, String msgCode, Object... args) {
        displayMessage(langBean, FacesMessage.SEVERITY_ERROR, msgCode, args);
    }

    public static void displayWarnMessage(LangBean langBean, String msgCode, Object... args) {
        displayMessage(langBean, FacesMessage.SEVERITY_WARN, msgCode, args);
    }

    public static void displayNoThesaurusConfiguredMessage(LangBean langBean) {
        displayErrorMessage(langBean, "common.error.thesaurusConfig.notfound");
    }

    public static void displayInternalError(LangBean langBean) {
        displayErrorMessage(langBean, "common.error.internal");
    }
}
