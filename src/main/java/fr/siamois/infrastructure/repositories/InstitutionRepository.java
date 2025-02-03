package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Institution;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InstitutionRepository extends CrudRepository<Institution, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT i.* FROM institution i " +
                    "JOIN person_role_institution pri ON i.institution_id = pri.fk_institution_id " +
                    "WHERE pri.fk_person_id = :personId"
    )
    List<Institution> findAllOfPerson(Long personId);

}
