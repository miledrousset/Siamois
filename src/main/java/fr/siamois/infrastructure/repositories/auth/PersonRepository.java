package fr.siamois.infrastructure.repositories.auth;

import fr.siamois.models.auth.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT p.* FROM person_role_team prt JOIN person p ON prt.fk_person_id = p.person_id WHERE prt.fk_team_id = :teamId"
    )
    List<Person> findPersonsOfTeam(@Param("teamId") Long teamId);

    @Query(
            nativeQuery = true,
            value = "SELECT p.* FROM person p JOIN system_role_user sru ON p.person_id = sru.person_id WHERE role_id = :systemRoleId"
    )
    List<Person> findPersonsWithSystemRole(@Param("systemRoleId") Long systemRoleId);

    @Query(
            nativeQuery = true,
            value = "SELECT p.* FROM person_role_team prt JOIN person p ON prt.fk_person_id = p.person_id WHERE prt.fk_team_id = :team AND prt.fk_role_concept_id = :roleId"
    )
    List<Person> findPersonsWithRole(@Param("roleId") Long roleConceptId);

    @Query(
            nativeQuery = true,
            value = "SELECT p.* FROM person_role_team prt " +
                    "JOIN person p ON prt.fk_person_id = p.person_id " +
                    "WHERE prt.fk_team_id = :teamId " +
                    "AND prt.fk_role_concept_id = :roleId"
    )
    List<Person> findPersonsOfTeamWithRole(@Param("teamId") Long teamId, @Param("roleId") Long roleConceptId);

    Optional<Person> findPersonByUsername(String username);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO person_role_team (fk_person_id, fk_team_id, fk_role_concept_id, is_manager) VALUES (:personId, :teamId, null, TRUE)"
    )
    int addManagerToTeam(Long personId, Long teamId);

    @Query(
            nativeQuery = true,
            value = "SELECT p.* FROM person p JOIN person_role_team prt ON p.person_id = prt.fk_person_id WHERE prt.fk_team_id = :teamId"
    )
    List<Person> findTeamMembers(Long teamId);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO person_role_team (fk_person_id, fk_team_id, fk_role_concept_id, is_manager) VALUES (:personId, :teamId, :roleId, FALSE)"
    )
    int addUserToTeam(@Param("personId") Long personId, @Param("teamId") Long teamId, @Param("roleId") Long roleId);
}
