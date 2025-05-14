package fr.siamois.ui.bean.settings.team;

import fr.siamois.domain.models.institution.Team;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.settings.components.OptionElement;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Component
public class TeamDetailsBean implements Serializable {

    private final TeamInfoBean teamInfoBean;
    private final TeamScopeBean teamScopeBean;
    private final TeamMembersBean teamMembersBean;
    private final LangBean langBean;
    private Team team;

    private transient List<OptionElement> elements;

    public TeamDetailsBean(TeamInfoBean teamInfoBean, TeamScopeBean teamScopeBean, TeamMembersBean teamMembersBean, LangBean langBean) {
        this.teamInfoBean = teamInfoBean;
        this.teamScopeBean = teamScopeBean;
        this.teamMembersBean = teamMembersBean;
        this.langBean = langBean;
    }

    public void init(Team team) {
        this.team = team;
        elements = new ArrayList<>();

        elements.add(new OptionElement("bi bi-file-text", langBean.msg("groupSettings.info"),
                langBean.msg("groupSettings.info.description"), () -> {
            teamInfoBean.init(team);
            return "/pages/settings/team/teamInfoSettings.xhtml?faces-redirect=true";
        }));

        elements.add(new OptionElement("bi bi-layers-half", langBean.msg("groupSettings.scope"),
                langBean.msg("groupSettings.scope.description"), () -> {
            teamScopeBean.init(team);
            return "/pages/settings/team/teamScopeSettings.xhtml?faces-redirect=true";
        }));

        elements.add(new OptionElement("bi bi-person-badge", langBean.msg("groupSettings.members"),
                langBean.msg("groupSettings.members.description"), () -> {
            teamMembersBean.init(team);
            return "/pages/settings/team/teamMembersSettings.xhtml?faces-redirect=true";
        }));
    }

    public String backToTeamDetails() {
        return "/pages/settings/team/teamDetailsSettings.xhtml?faces-redirect=true";
    }
}
