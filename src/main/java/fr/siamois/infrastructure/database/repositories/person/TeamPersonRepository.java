package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.team.Team;
import fr.siamois.domain.models.team.TeamPerson;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamPersonRepository extends CrudRepository<TeamPerson, TeamPerson.TeamPersonId> {
    List<TeamPerson> findByTeam(Team team);
}
