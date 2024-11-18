package fr.siamois.repositories;

import fr.siamois.models.Person;
import fr.siamois.models.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT prt.* FROM person_role_team prt JOIN team t ON prt.fk_team_id = t.team_id WHERE prt.fk_person_id = :person"
    )
    List<Team> findTeamsOfPerson(Person person);

}
