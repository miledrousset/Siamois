package fr.siamois.ui.bean;

import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.services.ArkManagerService;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.utils.CodeUtils;
import fr.siamois.domain.utils.MessageUtils;
import jakarta.faces.application.FacesMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@Slf4j
@Setter
@Getter
@Component
@SessionScoped
public class ArkBean implements Serializable {

    private final SessionSettingsBean sessionSettingsBean;
    private final transient InstitutionService institutionService;
    private final transient ArkManagerService arkManagerService;

    //  Fields
    // Toggles
    private boolean vArkServerIsActivated;

    // Local Ark Server
    private String vLocalNaan;
    private String vLocalPrefix;
    private int vLocalSize = 16;
    private boolean vLocalIsCaps = false;

    public ArkBean(SessionSettingsBean sessionSettingsBean,
                   InstitutionService institutionService,
                   ArkManagerService arkManagerService) {
        this.sessionSettingsBean = sessionSettingsBean;
        this.institutionService = institutionService;
        this.arkManagerService = arkManagerService;
    }

    public void loadExistingConfig() {
        InstitutionSettings settings = sessionSettingsBean.getInstitutionSettings();
        if (settings.hasEnabledArkConfig()) {
            vArkServerIsActivated = true;
            vLocalNaan = settings.getArkNaan();
            vLocalPrefix = settings.getArkPrefix();
            vLocalSize = settings.getArkSize();
            vLocalIsCaps = settings.getArkIsUppercase();
        }
    }

    public void saveArkConfig() {
        InstitutionSettings settings = sessionSettingsBean.getInstitutionSettings();
        settings.setArkNaan(vLocalNaan);
        settings.setArkPrefix(vLocalPrefix);
        settings.setArkSize(vLocalSize);
        settings.setArkIsUppercase(vLocalIsCaps);

        settings = institutionService.saveSettings(settings);
        sessionSettingsBean.setInstitutionSettings(settings);

        if (settings.hasEnabledArkConfig()) {
            arkManagerService.addArkToEntitiesWithoutArk(settings.getInstitution());
        }

        MessageUtils.displayMessage(FacesMessage.SEVERITY_INFO, "Success", "Configuration successfully saved");
    }

    public void changeToggle() {
        InstitutionSettings settings = sessionSettingsBean.getInstitutionSettings();

        settings.setArkIsEnabled(vArkServerIsActivated);

        if (settings.hasEnabledArkConfig()) {
            arkManagerService.addArkToEntitiesWithoutArk(settings.getInstitution());
        }

        settings = institutionService.saveSettings(settings);
        sessionSettingsBean.setInstitutionSettings(settings);
    }

    public String randomCode() {
        return CodeUtils.generateCode(5);
    }
}
