package fr.siamois.ui.bean.settings.team;

import fr.siamois.domain.models.Team;
import fr.siamois.ui.bean.settings.components.OptionElement;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Component
public class TeamDetailsBean implements Serializable {

    private Team team;

    private transient List<OptionElement> elements;

    public void init(Team team) {
        this.team = team;
    }
}
