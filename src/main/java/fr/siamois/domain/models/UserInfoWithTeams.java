package fr.siamois.domain.models;

import fr.siamois.domain.models.institution.Team;
import lombok.Getter;

import java.util.SortedSet;

public class UserInfoWithTeams extends UserInfo {

    @Getter
    private final SortedSet<Team> teams;

    public UserInfoWithTeams(UserInfo userInfo, SortedSet<Team> teams) {
        super(userInfo.getInstitution(), userInfo.getUser(), userInfo.getLang());
        this.teams = teams;
    }
}
