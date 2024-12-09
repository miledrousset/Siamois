package fr.siamois.bean;

import fr.siamois.services.LangService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@SessionScoped
public class LangBean implements Serializable {

    private final LangService langService;
    private final MessageSource messageSource;

    @Value("${siamois.lang.default}")
    private String defaultLang;

    private Locale locale = new Locale("en");

    public LangBean(MessageSource messageSource, LangService langService) {
        this.messageSource = messageSource;
        this.langService = langService;
    }

    @PostConstruct
    public void setPropertiesLang() {
        if (!StringUtils.isEmpty(defaultLang)) {
            setLanguage(defaultLang);
        }
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

    public List<String> getLangs() {
        return langService.getAvailableLanguages()
                .stream()
                .map(lang -> "'" + lang + "'")
                .toList();
    }

}
