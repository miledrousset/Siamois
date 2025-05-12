package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.institution.Institution;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.settings.components.OptionElement;
import fr.siamois.ui.bean.settings.team.TeamListBean;
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
    private final LangBean langBean;
    private final TeamListBean teamListBean;
    private Institution institution;
    private transient List<OptionElement> elements;

    public InstitutionDetailsBean(InstitutionInfoSettingsBean institutionInfoSettingsBean,
                                  InstitutionManagerSettingsBean institutionManagerSettingsBean, InstitutionThesaurusSettingsBean institutionThesaurusSettingsBean, LangBean langBean, TeamListBean teamListBean) {
        this.institutionInfoSettingsBean = institutionInfoSettingsBean;
        this.institutionManagerSettingsBean = institutionManagerSettingsBean;
        this.institutionThesaurusSettingsBean = institutionThesaurusSettingsBean;
        this.langBean = langBean;
        this.teamListBean = teamListBean;
    }

    public void addInstitutionManagementElements() {
        elements = new ArrayList<>();
        elements.add(new OptionElement("bi bi-building", langBean.msg("organisationSettings.titles.settings"),
                langBean.msg("organisationSettings.descriptions.settings"), () -> {
            institutionInfoSettingsBean.init(institution);
            return "/pages/settings/institution/institutionInfoSettings.xhtml?faces-redirect=true";
        }));
        elements.add(new OptionElement("bi bi-person-circle", langBean.msg("organisationSettings.titles.managers"),
                langBean.msg("organisationSettings.descriptions.managers", institution.getName()), () -> {
            institutionManagerSettingsBean.init(institution);
            return "/pages/settings/institution/institutionManagerSettings.xhtml?faces-redirect=true";
        }));
        elements.add(new OptionElement("bi bi-people", langBean.msg("organisationSettings.titles.teams"),
                langBean.msg("organisationSettings.descriptions.teams", institution.getName()), () -> {
            teamListBean.init(institution);
            return "/pages/settings/team/teamList.xhtml?faces-redirect=true";
        }));
        elements.add(new OptionElement("bi bi-table", langBean.msg("common.label.thesaurus"),
                langBean.msg("organisationSettings.descriptions.thesaurus"), () -> {
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
