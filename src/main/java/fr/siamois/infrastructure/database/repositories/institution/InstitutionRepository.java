package fr.siamois.infrastructure.database.repositories.institution;

import fr.siamois.domain.models.institution.Institution;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InstitutionRepository extends CrudRepository<Institution, Long> {

    Optional<Institution> findInstitutionByIdentifier(@NotNull String identifier);

    @Query(
            nativeQuery = true,
            value = "SELECT DISTINCT i.* FROM institution i " +
                    "JOIN team t ON t.fk_institution_id = i.institution_id " +
                    "JOIN team_person tp ON tp.fk_team_id = t.team_id " +
                    "WHERE tp.fk_person_id = :personId"
    )
    List<Institution> findAllOfPerson(Long personId);

    @Query(
            nativeQuery = true,
            value = "SELECT DISTINCT i.* FROM institution i " +
                    "JOIN institution_manager im ON im.fk_institution_id = i.institution_id " +
                    "WHERE im.fk_person_id = :personId"
    )
    List<Institution> findAllManagedByPerson(Long personId);

    @Query(
            nativeQuery = true,
            value = "SELECT COUNT(*) >= 1 " +
                    "FROM institution_manager im " +
                    "WHERE im.fk_person_id = :personId AND im.fk_institution_id = :institutionId"
    )
    boolean personIsInstitutionManager(Long institutionId, Long personId);

}
