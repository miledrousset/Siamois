package fr.siamois.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
public class LangService {

    private final MessageSource messageSource;

    public LangService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Get message from key
     * @param key key of the message
     * @return message
     */
    public String msg(String key, Locale locale) {
        log.trace("Getting message for key: {}", key);
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
        List<String> languages = new ArrayList<>();

        if (files == null) return languages;

        for (File file : files) {
            if (file.getName().startsWith("messages_") && file.getName().endsWith(".properties")) {
                languages.add(file.getName().replace("messages_", "").replace(".properties", ""));
            }
        }

        return languages;
    }

}
