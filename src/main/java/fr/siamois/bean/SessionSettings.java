package fr.siamois.bean;

import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;

@Component
@Getter
@Setter
@SessionScoped
public class SessionSettings {

    private Person authenticatedUser;
    private Team selectedTeam;

}
