package fr.siamois.infrastructure.repositories.auth;

import fr.siamois.models.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT t.* FROM person_role_team prt " +
                    "JOIN team t ON prt.fk_team_id = t.team_id " +
                    "WHERE prt.fk_person_id = :personId"
    )
    List<Team> findTeamsOfPerson(@Param("personId") Long personId);

    Optional<Team> findTeamByNameIgnoreCase(String teamName);

}
