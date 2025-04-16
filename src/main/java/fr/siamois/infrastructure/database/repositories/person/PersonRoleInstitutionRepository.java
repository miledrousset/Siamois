package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.settings.PersonRoleInstitution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRoleInstitutionRepository extends CrudRepository<PersonRoleInstitution, PersonRoleInstitution.PersonRoleInstitutionId> {

    Optional<PersonRoleInstitution> findByInstitutionAndPerson(Institution institution, Person person);

    boolean findByInstitutionAndPersonAndIsManager(Institution institution, Person person, Boolean isManager);
}
