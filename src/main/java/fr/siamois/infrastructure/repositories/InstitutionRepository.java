package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Institution;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InstitutionRepository extends CrudRepository<Institution, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT i.* FROM institution i " +
                    "JOIN person_role_institution pri ON i.institution_id = pri.fk_institution_id " +
                    "WHERE pri.fk_person_id = :personId"
    )
    List<Institution> findAllOfPerson(Long personId);

    Optional<Institution> findInstitutionByCode(@NotNull String code);

    @Query(
            nativeQuery = true,
            value = "SELECT i.* FROM institution i WHERE i.fk_manager_id = :personId"
    )
    List<Institution> findAllManagedBy(Long personId);
}
