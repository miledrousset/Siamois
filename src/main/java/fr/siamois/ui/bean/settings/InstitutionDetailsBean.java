package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.Institution;
import fr.siamois.ui.bean.settings.components.OptionElement;
import fr.siamois.ui.bean.settings.institution.InstitutionInfoSettingsBean;
import fr.siamois.ui.bean.settings.institution.InstitutionManagerSettingsBean;
import fr.siamois.ui.bean.settings.institution.InstitutionThesaurusSettingsBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class InstitutionDetailsBean implements Serializable {

    private final InstitutionInfoSettingsBean institutionInfoSettingsBean;
    private final InstitutionManagerSettingsBean institutionManagerSettingsBean;
    private final InstitutionThesaurusSettingsBean institutionThesaurusSettingsBean;
    private Institution institution;
    private transient List<OptionElement> elements;

    public InstitutionDetailsBean(InstitutionInfoSettingsBean institutionInfoSettingsBean,
                                  InstitutionManagerSettingsBean institutionManagerSettingsBean, InstitutionThesaurusSettingsBean institutionThesaurusSettingsBean) {
        this.institutionInfoSettingsBean = institutionInfoSettingsBean;
        this.institutionManagerSettingsBean = institutionManagerSettingsBean;
        this.institutionThesaurusSettingsBean = institutionThesaurusSettingsBean;
    }

    public void addInstitutionManagementElements() {
        elements = new ArrayList<>();
        elements.add(new OptionElement("bi bi-building", "Paramètres de l'organisation", "Nom de l'organisation", () -> {
            institutionInfoSettingsBean.init(institution);
            return "/pages/settings/institution/institutionInfoSettings.xhtml?faces-redirect=true";
        }));
        elements.add(new OptionElement("bi bi-person-circle", "Responsables d'organisation", "Gérer les administrateurs de Bibracte", () -> {
            institutionManagerSettingsBean.init(institution);
            return "/pages/settings/institution/institutionManagerSettings.xhtml?faces-redirect=true";
        }));
        elements.add(new OptionElement("bi bi-people", "Équipes", "Gérer les équipes de Bibracte", () -> {
            log.trace("Clicked on institution teams");
            return "";
        }));
        elements.add(new OptionElement("bi bi-table", "Thesaurus","Configurer le thesaurus utilisé par l'organisation", () -> {
            institutionThesaurusSettingsBean.init(institution);
            return "/pages/settings/institution/thesaurusSettings.xhtml?faces-redirect=true";
        }));
    }

    public String goToInstitutionList() {
        institution = null;
        elements.clear();
        return "/pages/settings/institutionListSettings.xhtml?faces-redirect=true";
    }

    public String backToInstitutionSettings() {
        return "/pages/settings/institutionSettings.xhtml?faces-redirect=true";
    }

}
