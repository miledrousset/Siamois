package fr.siamois.ui.bean.settings.team;

import fr.siamois.domain.models.institution.Team;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Setter
@Component
public class TeamScopeBean implements Serializable {

    private Team team;

    public void init(Team team) {
        this.team = team;
    }
}
