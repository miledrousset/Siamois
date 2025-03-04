package fr.siamois.infrastructure.repositories;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Modifying;
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

    Optional<Institution> findInstitutionByIdentifier(@NotNull String identifier);

    @Query(
            nativeQuery = true,
            value = "SELECT i.* FROM institution i WHERE i.fk_manager_id = :personId"
    )
    List<Institution> findAllManagedBy(Long personId);

    @Query(
            nativeQuery = true,
            value = "SELECT COUNT(*) == 1 FROM person_role_institution pri " +
                    "WHERE pri.fk_institution_id = :institutionId AND " +
                    "pri.fk_person_id = :personId"
    )
    boolean personExistInInstitution(Long personId, Long institutionId);

    @Modifying
    @Transactional
    @Query(
            nativeQuery = true,
            value = "INSERT INTO person_role_institution(fk_person_id, fk_role_concept_id, fk_institution_id) " +
                    "VALUES (:personId, NULL, :institutionId)"
    )
    void addPersonTo(Long personId, Long institutionId);

    @Modifying
    @Transactional
    @Query(
            nativeQuery = true,
            value = "UPDATE person_role_institution " +
                    "SET is_manager = TRUE " +
                    "WHERE fk_institution_id = :institutionId AND fk_person_id = :personId"
    )
    void setPersonAsManagerOf(Long personId, Long institutionId);

    @Query(
            nativeQuery = true,
            value = "SELECT pri.is_manager FROM person_role_institution pri " +
                    "WHERE pri.fk_person_id = :personId AND pri.fk_institution_id = :institutionId"
    )
    boolean isManagerOf(Long institutionId, Long personId);

}
