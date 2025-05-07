package fr.siamois.infrastructure.database.repositories.institution;

import fr.siamois.domain.models.Institution;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InstitutionRepository extends CrudRepository<Institution, Long> {

    Optional<Institution> findInstitutionByIdentifier(@NotNull String identifier);

    @Query(
            nativeQuery = true,
            value = "SELECT i.* FROM institution i WHERE i.fk_manager_id = :personId"
    )
    List<Institution> findAllManagedBy(Long personId);

    @Query(
            nativeQuery = true,
            value = "SELECT i.* FROM institution i " +
                    "JOIN person_role_institution pri ON i.institution_id = pri.fk_institution_id " +
                    "WHERE pri.fk_person_id = :personId"
    )
    List<Institution> findAllOfPerson(Long personId);

}
