package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.settings.PersonRoleInstitution;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRoleInstitutionRepository extends CrudRepository<PersonRoleInstitution, PersonRoleInstitution.PersonRoleInstitutionId> {

    @Query(
            nativeQuery = true,
            value = "SELECT i.* FROM institution i " +
                    "JOIN person_role_institution pri ON i.institution_id = pri.fk_institution_id " +
                    "WHERE pri.fk_person_id = :personId"
    )
    List<Institution> findAllOfPerson(Long personId);

    Optional<PersonRoleInstitution> findByInstitutionAndPerson(Institution institution, Person person);

    boolean findByInstitutionAndPersonAndIsManager(Institution institution, Person person, Boolean isManager);
}
