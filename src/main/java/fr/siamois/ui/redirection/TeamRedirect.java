package fr.siamois.ui.redirection;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.ui.bean.settings.InstitutionDetailsBean;
import fr.siamois.ui.bean.settings.InstitutionListSettingsBean;
import fr.siamois.ui.bean.settings.team.TeamListBean;
import fr.siamois.ui.bean.settings.team.TeamMembersBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;

@SessionScoped
@Controller
public class TeamRedirect {

    private final InstitutionListSettingsBean institutionListSettingsBean;
    private final ActionUnitService actionUnitService;
    private final TeamMembersBean teamMembersBean;
    private final InstitutionDetailsBean institutionDetailsBean;
    private final TeamListBean teamListBean;

    public TeamRedirect(InstitutionListSettingsBean institutionListSettingsBean,
                        ActionUnitService actionUnitService,
                        TeamMembersBean teamMembersBean,
                        InstitutionDetailsBean institutionDetailsBean,
                        TeamListBean teamListBean) {
        this.institutionListSettingsBean = institutionListSettingsBean;
        this.actionUnitService = actionUnitService;
        this.teamMembersBean = teamMembersBean;
        this.institutionDetailsBean = institutionDetailsBean;
        this.teamListBean = teamListBean;
    }

    @GetMapping("/settings/organisation/actionunit/{actionUnitId}/members")
    public String redirectToTeam(@PathVariable Long actionUnitId) {
        ActionUnit actionUnit = actionUnitService.findById(actionUnitId);
        institutionListSettingsBean.init();
        institutionDetailsBean.setInstitution(actionUnit.getCreatedByInstitution());
        institutionDetailsBean.init();
        institutionDetailsBean.setInstitution(actionUnit.getCreatedByInstitution());
        teamListBean.init(actionUnit.getCreatedByInstitution());
        teamMembersBean.init(actionUnit);
        return "forward:/pages/settings/team/manageTeamMember.xhtml";
    }

}
