package fr.siamois.domain.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Service to manage the language of the application
 *
 * @author Julien Linget
 */
@Slf4j
@Service
public class LangService {

    private final MessageSource messageSource;

    @Getter
    @Value("${siamois.lang.default}")
    private String defaultLang;

    public LangService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Get message from key
     * @param key key of the message
     * @return message
     */
    public String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    /**
     * Get message formatted with args. Applies {@link String#format(String, Object...)} on the message with the args.
     * @param format format of the message
     * @param args arguments to format
     * @return formatted message
     */
    public String msg(String format, Locale locale, Object... args) {
        return String.format(msg(format, locale), args);
    }

    public List<String> getAvailableLanguages() {
        String path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("language")).getPath();
        File[] files = new File(path).listFiles();
        Set<String> languages = new TreeSet<>();

        if (files == null) return List.of();

        for (File file : files) {
            if (file.getName().startsWith("messages_") && file.getName().endsWith(".properties")) {
                languages.add(file.getName().replace("messages_", "").replace(".properties", ""));
            }
        }

        languages.add("en");

        return languages.stream().toList();
    }

}
