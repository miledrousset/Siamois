package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.institution.Team;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {
    List<Team> findTeamsByInstitution(Institution institution);

    @Query(
            nativeQuery = true,
            value = "SELECT COUNT(*) FROM team_person WHERE fk_team_id = :teamId"
    )
    long countMembersOfTeam(Long teamId);

    @Query(
            nativeQuery = true,
            value = "SELECT t.* FROM team t WHERE t.fk_institution_id = :institutionId AND UPPER(t.name) = UPPER(:teamName)"
    )
    Optional<Team> findTeamByNameInInstitution(Long institutionId, @NotNull String teamName);
}
