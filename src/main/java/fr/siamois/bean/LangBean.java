package fr.siamois.bean;

import fr.siamois.services.LangService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${siamois.lang.default}")
    private String defaultLang;

    private Locale locale = new Locale("fr");

    public LangBean(LangService langService) {
        this.langService = langService;
    }

    /**
     * Load default language from properties file if set
     */
    @PostConstruct
    public void setPropertiesLang() {
        if (!StringUtils.isEmpty(defaultLang)) {
            setLanguage(defaultLang);
        }
    }

    /**
     * Get message from key
     * @param key key of the message
     * @return message
     */
    public String msg(String key) {
        return langService.msg(key, locale);
    }

    /**
     * Get message formatted with args. Applies {@link String#format(String, Object...)} on the message with the args.
     * @param format format of the message
     * @param args arguments to format
     * @return formatted message
     */
    public String msg(String format, Object... args) {
        return langService.msg(format, locale, args);
    }

    /**
     * Changes the language with the given language code (e.g. "en", "fr", "de")
     * @param lang language code
     */
    public void setLanguage(String lang) {
        log.trace("Setting language to {}", lang);
        locale = new Locale(lang);
    }

    /**
     * Get the current language code
     * @return language code
     */
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
