package fr.siamois.repositories;

import fr.siamois.models.Concept;
import fr.siamois.models.Person;
import fr.siamois.models.SystemRole;
import fr.siamois.models.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends CrudRepository<Person, Integer> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM person_role_team prt JOIN person p ON prt.fk_person_id = p.person_id WHERE prt.fk_team_id = :team"
    )
    List<Person> findPersonsOfTeam(@Param("team") Team team);

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM system_role sr JOIN public.system_role_user sru on sr.system_role_id = sru.role_id WHERE sru.role_id = :systemRole"
    )
    List<Person> findPersonsWithSystemRole(@Param("systemRole") SystemRole systemRole);

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM person_role_team prt JOIN person p ON prt.fk_person_id = p.person_id WHERE prt.fk_team_id = :team AND prt.fk_role_concept_id = :role"
    )
    List<Person> findPersonsWithRole(@Param("role") Concept role);

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM person_role_team prt " +
                    "JOIN person p ON prt.fk_person_id = p.person_id " +
                    "WHERE prt.fk_team_id = :team " +
                    "AND prt.fk_role_concept_id = :role"
    )
    List<Person> findPersonsOfTeamWithRole(@Param("team") Team team, @Param("role") Concept role);

}
