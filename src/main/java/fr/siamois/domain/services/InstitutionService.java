package fr.siamois.domain.services;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.institution.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.institution.InstitutionAlreadyExistException;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.models.settings.PersonRoleInstitution;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.database.repositories.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRoleInstitutionRepository;
import fr.siamois.infrastructure.database.repositories.settings.InstitutionSettingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionSettingsRepository institutionSettingsRepository;
    private final PersonRepository personRepository;
    private final PersonRoleInstitutionRepository personRoleInstitutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository, PersonRepository personRepository, InstitutionSettingsRepository institutionSettingsRepository, PersonRoleInstitutionRepository personRoleInstitutionRepository) {
        this.institutionRepository = institutionRepository;
        this.personRepository = personRepository;
        this.institutionSettingsRepository = institutionSettingsRepository;
        this.personRoleInstitutionRepository = personRoleInstitutionRepository;
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

    public Institution createInstitution(Institution institution) throws InstitutionAlreadyExistException, FailedInstitutionSaveException {
        Optional<Institution> existing = institutionRepository.findInstitutionByIdentifier(institution.getIdentifier());
        if (existing.isPresent()) throw new InstitutionAlreadyExistException("Institution with code " + institution.getIdentifier() + " already exists");
        try {
            return institutionRepository.save(institution);
        } catch (Exception e) {
            log.error("Error while saving institution", e);
            throw new FailedInstitutionSaveException("Failed to save institution");
        }
    }

    public List<Person> findMembersOf(Institution institution) {
        List<Person> members = personRepository.findMembersOfInstitution(institution.getId());
        boolean managerIsPresent = members.stream()
                .anyMatch(member -> Objects.equals(member.getId(), institution.getManager().getId()));

        if (!managerIsPresent) {
            members.add(institution.getManager());
        }

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
        Optional<PersonRoleInstitution> opt = personRoleInstitutionRepository.findByInstitutionAndPerson(institution, person);
        if (opt.isEmpty()) {
            PersonRoleInstitution personRoleInstitution = new PersonRoleInstitution();
            personRoleInstitution.setId(new PersonRoleInstitution.PersonRoleInstitutionId());
            personRoleInstitution.getId().setFkInstitutionId(institution.getId());
            personRoleInstitution.getId().setFkPersonId(person.getId());
            personRoleInstitution.setPerson(person);
            personRoleInstitution.setInstitution(institution);
            personRoleInstitution.setRoleConcept(null);
            personRoleInstitution.setIsManager(true);
            personRoleInstitutionRepository.save(personRoleInstitution);
        } else {
            PersonRoleInstitution personRoleInstitution = opt.get();
            if (Boolean.FALSE.equals(personRoleInstitution.getIsManager())) {
                personRoleInstitution.setIsManager(true);
                personRoleInstitutionRepository.save(personRoleInstitution);
            }
        }
    }

    public boolean isManagerOf(Institution institution, Person person) {
        if (Objects.equals(institution.getManager(), person))
            return true;
        return personRoleInstitutionRepository.personExistInInstitution(institution.getId(), person.getId(), true);
    }

    public Institution update(Institution institution) {
        return institutionRepository.save(institution);
    }

    public long countMembersInInstitution(Institution institution) {
        return findMembersOf(institution).size();
    }

    public Optional<PersonRoleInstitution> findPersonInInstitution(Institution institution, Person person) {
        return personRoleInstitutionRepository.findByInstitutionAndPerson(institution, person);
    }

}
