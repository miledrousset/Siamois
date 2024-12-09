package fr.siamois.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class LangService {

    public List<String> getAvailableLanguages() {
        String path = Thread.currentThread().getContextClassLoader().getResource("language").getPath();
        File[] files = new File(path).listFiles();
        List<String> languages = new ArrayList<>();

        for (File file : files) {
            if (file.getName().startsWith("messages_") && file.getName().endsWith(".properties")) {
                languages.add(file.getName().replace("messages_", "").replace(".properties", ""));
            }
        }

        return languages;
    }

}
