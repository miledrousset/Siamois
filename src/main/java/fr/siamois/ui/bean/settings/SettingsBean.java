package fr.siamois.ui.bean.settings;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class SettingsBean {

    public void save() {
        // TODO document why this method is empty
    }

}
