package fr.siamois.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Locale;

@Slf4j
@Component
@SessionScoped
public class LangBean implements Serializable {

    private final MessageSource messageSource;
    private  Locale locale = new Locale("fr");

    public LangBean(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String msg(String key) {
        return messageSource.getMessage(key, null, locale);
    }

    public void setLanguage(String lang) {
        log.trace("Setting language to {}", lang);
        locale = new Locale(lang);
    }

    public String getLanguageCode() {
        return locale.getLanguage();
    }

}
