package fr.siamois.bean;

import fr.siamois.models.Institution;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.utils.AuthenticatedUserUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;

@Setter
@Getter
@Component
@SessionScoped
public class SessionSettings {

    @Getter
    private Institution selectedInstitution;

    public Person getAuthenticatedUser() {
        return AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow(() -> new RuntimeException("No authenticated user"));
    }

    @Deprecated
    public Team getSelectedTeam() {
        return null;
    }

}
