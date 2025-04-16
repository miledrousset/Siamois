package fr.siamois.ui.bean.dialog.institution;

import fr.siamois.ui.email.EmailManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@Slf4j
@Component
@SessionScoped
@Getter
@Setter
public class UserDialogBean implements Serializable {

    private final EmailManager emailManager;
    private String title;
    private String buttonLabel;

    private String userEmail;

    public UserDialogBean(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    public void reset() {
        log.trace("Reset users");
    }

    public void save() {
        emailManager.sendEmail("siamois@siamois.fr", title, buttonLabel);
    }

    public void exit() {
        reset();
        PrimeFaces.current().executeScript("PF('newManagerDialog').exit();");
    }

}
