package fr.siamois.ui.bean.settings.team;

import fr.siamois.domain.models.Team;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Setter
@Getter
@Component
public class TeamMembersBean implements Serializable {

    private Team team;

    public void init(Team team) {
        this.team = team;
    }
}
