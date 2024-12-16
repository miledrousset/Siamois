package fr.siamois.bean;

import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoTeamSelectedException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;

@Component
@Setter
@SessionScoped
public class SessionSettings {

    @Getter
    private Person authenticatedUser;

    private Team selectedTeam;

    public Team getSelectedTeam() throws NoTeamSelectedException {
        if (selectedTeam == null) {
            throw new NoTeamSelectedException();
        }
        return selectedTeam;
    }

}
