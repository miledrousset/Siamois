package fr.siamois.infrastructure.database.repositories.team;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.team.TeamMemberRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TeamMemberRepository extends CrudRepository<TeamMemberRelation, TeamMemberRelation.TeamMemberId> {

    @Query(
            nativeQuery = true,
            value = "SELECT DISTINCT p.* FROM person p " +
                    "JOIN team_member tm ON p.person_id = tm.fk_person_id " +
                    "JOIN action_unit au ON tm.fk_action_unit_id = au.action_unit_id " +
                    "WHERE au.fk_institution_id = :institutionId"
    )
    Set<Person> findAllByInstitution(Long institutionId);
}
