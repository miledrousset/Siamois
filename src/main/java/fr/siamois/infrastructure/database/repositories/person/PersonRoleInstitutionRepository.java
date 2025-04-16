package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.settings.PersonRoleInstitution;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRoleInstitutionRepository extends CrudRepository<PersonRoleInstitution, PersonRoleInstitution.PersonRoleInstitutionId> {

    Optional<PersonRoleInstitution> findByInstitutionAndPerson(Institution institution, Person person);

    @Query(
            nativeQuery = true,
            value = "SELECT count(*) = 1 " +
                    "FROM person_role_institution pri " +
                    "WHERE pri.fk_institution_id = :institutionId " +
                    "AND pri.fk_person_id = :personId " +
                    "AND pri.is_manager = :isManager"
    )
    boolean personExistInInstitution(Long institutionId, Long personId, Boolean isManager);
}
