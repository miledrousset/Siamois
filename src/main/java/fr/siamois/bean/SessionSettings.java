package fr.siamois.bean;

import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoTeamSelectedException;
import fr.siamois.utils.AuthenticatedUserUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Setter
@Getter
@Component
@SessionScoped
public class SessionSettings {

    private Team selectedTeam;

    public Person getAuthenticatedUser() {
        return AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow(() -> new RuntimeException("No authenticated user"));
    }

    public Team getSelectedTeam() throws NoTeamSelectedException {
        if (selectedTeam == null) {
            throw new NoTeamSelectedException();
        }
        return selectedTeam;
    }

}
