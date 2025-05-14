package fr.siamois.ui.bean.settings.institution;

import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.publisher.InstitutionChangeEventPublisher;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import jakarta.faces.application.FacesMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@Getter
@Setter
@Component
@SessionScoped
public class InstitutionInfoSettingsBean implements Serializable {

    private final transient InstitutionService institutionService;
    private final transient InstitutionChangeEventPublisher institutionChangeEventPublisher;
    private final LangBean langBean;
    private Institution institution;
    private String fName;

    public InstitutionInfoSettingsBean(InstitutionService institutionService,
                                       InstitutionChangeEventPublisher institutionChangeEventPublisher, LangBean langBean) {
        this.institutionService = institutionService;
        this.institutionChangeEventPublisher = institutionChangeEventPublisher;
        this.langBean = langBean;
    }

    public void init(Institution institution) {
        this.institution = institution;
        fName = institution.getName();
    }

    public void updateName() {
        if (fName != null && !fName.equalsIgnoreCase(institution.getName())) {
            institution.setName(fName);
            institution = institutionService.update(institution);
            institutionChangeEventPublisher.publishInstitutionChangeEvent();
            MessageUtils.displayInfoMessage(langBean, "organisationSettings.action.infoUpdated");
        } else {
            MessageUtils.displayMessage(langBean, FacesMessage.SEVERITY_WARN, "organisationSettings.action.unchanged");
        }
    }

}
