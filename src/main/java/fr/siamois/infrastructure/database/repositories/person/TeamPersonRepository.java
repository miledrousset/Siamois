package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.models.institution.TeamPerson;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamPersonRepository extends CrudRepository<TeamPerson, TeamPerson.TeamPersonId> {
    List<TeamPerson> findByTeam(Team team);

    @Query(
            nativeQuery = true,
            value = "SELECT tp.* FROM team_person tp " +
                    "JOIN team t ON tp.fk_team_id = t.team_id " +
                    "JOIN institution i ON t.fk_institution_id = i.institution_id " +
                    "WHERE i.institution_id = :institutionId"
    )
    List<TeamPerson> findAllOfInstitution(Long institutionId);

    @Query(
            nativeQuery = true,
            value = "SELECT MIN(tp.add_date) FROM team_person tp " +
                    "JOIN team t ON tp.fk_team_id = t.team_id " +
                    "WHERE t.fk_institution_id = :institutionId AND tp.fk_person_id = :personId"
    )
    OffsetDateTime findEarliestAddDateInInstitution(Long institutionId, Long personId);

    @Query(
            nativeQuery = true,
            value = "SELECT tp.* FROM team_person tp " +
                    "JOIN team t ON tp.fk_team_id = t.team_id " +
                    "WHERE t.fk_institution_id = :institutionId AND t.is_default_team IS TRUE"
    )
    Optional<TeamPerson> findDefaultOfInstitution(Long institutionId);

    Optional<TeamPerson> findByPersonAndTeam(Person person, Team team);
}
