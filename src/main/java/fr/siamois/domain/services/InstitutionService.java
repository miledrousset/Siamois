package fr.siamois.domain.services;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.InstitutionAlreadyExist;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.repositories.InstitutionRepository;
import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.infrastructure.repositories.settings.InstitutionSettingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionSettingsRepository institutionSettingsRepository;
    private final PersonRepository personRepository;

    public InstitutionService(InstitutionRepository institutionRepository, PersonRepository personRepository, InstitutionSettingsRepository institutionSettingsRepository) {
        this.institutionRepository = institutionRepository;
        this.personRepository = personRepository;
        this.institutionSettingsRepository = institutionSettingsRepository;
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

    public InstitutionSettings createOrGetSettingsOf(Institution institution) {
        Optional<InstitutionSettings> opt = institutionSettingsRepository.findById(institution.getId());
        if (opt.isPresent()) return opt.get();
        InstitutionSettings empty = new InstitutionSettings();
        empty.setInstitution(institution);
        return saveSettings(empty);
    }

    public InstitutionSettings saveSettings(InstitutionSettings settings) {
        return institutionSettingsRepository.save(settings);
    }

    public void addToManagers(Institution institution, Person person) {
        boolean personExistInInstitution = institutionRepository.personExistInInstitution(person.getId(), institution.getId());
        if (!personExistInInstitution) {
            institutionRepository.addPersonTo(person.getId(), institution.getId());
        }
        institutionRepository.setPersonAsManagerOf(person.getId(), institution.getId());
    }

    public boolean isManagerOf(Institution institution, Person person) {
        if (institution.getManager().equals(person))
            return true;
        return institutionRepository.isManagerOf(institution.getId(), person.getId());
    }

}
