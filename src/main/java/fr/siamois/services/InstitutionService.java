package fr.siamois.services;

import fr.siamois.infrastructure.repositories.InstitutionRepository;
import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.FailedInstitutionSaveException;
import fr.siamois.models.exceptions.InstitutionAlreadyExist;
import fr.siamois.models.vocabulary.Concept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final PersonRepository personRepository;

    public InstitutionService(InstitutionRepository institutionRepository, PersonRepository personRepository) {
        this.institutionRepository = institutionRepository;
        this.personRepository = personRepository;
    }

    public List<Institution> findAll() {
        List<Institution> result = new ArrayList<>();
        for (Institution institution : institutionRepository.findAll())
            result.add(institution);
        return result;
    }

    public List<Institution> findInstitutionsOfPerson(Person person) {
        List<Institution> notManaged = institutionRepository.findAllOfPerson(person.getId());
        List<Institution> managed = institutionRepository.findAllManagedBy(person.getId());
        managed.addAll(notManaged);
        return managed;
    }

    public List<Person> findAllManagers() {
        return personRepository.findAllInstitutionManagers();
    }

    public void createInstitution(Institution institution) throws InstitutionAlreadyExist, FailedInstitutionSaveException {
        Optional<Institution> existing = institutionRepository.findInstitutionByIdentifier(institution.getIdentifier());
        if (existing.isPresent()) throw new InstitutionAlreadyExist("Institution with code " + institution.getIdentifier() + " already exists");
        try {
            institutionRepository.save(institution);
        } catch (Exception e) {
            log.error("Error while saving institution", e);
            throw new FailedInstitutionSaveException("Institution with code " + institution.getIdentifier() + " already exists");
        }
    }

    public List<Person> findMembersOf(Institution institution) {
        List<Person> members = personRepository.findMembersOfInstitution(institution.getId());
        members.add(institution.getManager());
        return members;
    }

    public void addUserToInstitution(Person person, Institution institution, Concept roleConcept) throws FailedInstitutionSaveException {
        try {
            personRepository.addPersonToInstitution(person.getId(), institution.getId(), roleConcept.getId());
        } catch (Exception e) {
            log.error("Failed to add user to institution", e);
            throw new FailedInstitutionSaveException("Failed to add user to institution");
        }
    }
}
