package fr.siamois.domain.services;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.institution.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.institution.InstitutionAlreadyExistException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.models.team.ActionManagerRelation;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.database.repositories.institution.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.settings.InstitutionSettingsRepository;
import fr.siamois.infrastructure.database.repositories.team.ActionManagerRepository;
import fr.siamois.infrastructure.database.repositories.team.TeamMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionSettingsRepository institutionSettingsRepository;
    private final PersonRepository personRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ActionManagerRepository actionManagerRepository;


    public InstitutionService(InstitutionRepository institutionRepository,
                              PersonRepository personRepository,
                              InstitutionSettingsRepository institutionSettingsRepository, TeamMemberRepository teamMemberRepository, ActionManagerRepository actionManagerRepository) {
        this.institutionRepository = institutionRepository;
        this.personRepository = personRepository;
        this.institutionSettingsRepository = institutionSettingsRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.actionManagerRepository = actionManagerRepository;
    }

    public Set<Institution> findAll() {
        Set<Institution> result = new HashSet<>();
        for (Institution institution : institutionRepository.findAll())
            result.add(institution);
        return result;
    }

    public Set<Institution> findInstitutionsOfPerson(Person person) {
        Set<Institution> institutions = new HashSet<>();
        institutions.addAll(institutionRepository.findAllOfPerson(person.getId()));
        institutions.addAll(institutionRepository.findAllManagedByPerson(person.getId()));
        return institutions;
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

    public Set<Person> findMembersOf(Institution institution) {
        Set<Person> result = new HashSet<>(teamMemberRepository.findAllByInstitution(institution.getId()));

        List<Person> actionManagers = actionManagerRepository.findAllByInstitution(institution).stream()
                .map(ActionManagerRelation::getPerson)
                .toList();

        result.addAll(actionManagers);

        return result;
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

    public boolean addToManagers(Institution institution, Person person) {
        boolean result = institution.getManagers().add(person);
        institutionRepository.save(institution);
        return result;
    }

    public boolean isManagerOf(Institution institution, Person person) {
        return institution.getManagers().contains(person);
    }

    public Institution update(Institution institution) {
        return institutionRepository.save(institution);
    }

    public long countMembersInInstitution(Institution institution) {
        return findMembersOf(institution).size();
    }

    public boolean personIsInInstitution(Person person, Institution institution) {
        Optional<ActionManagerRelation> optManager = actionManagerRepository.findByPersonAndInstitution(person, institution);
        if (optManager.isPresent()) {
            return true;
        }

        return teamMemberRepository.personIsInInstitution(person.getId(), institution.getId());
    }

    public boolean personIsInstitutionManager(Person person, Institution institution) {
        return institutionRepository.personIsInstitutionManager(institution.getId(), person.getId());
    }

    public boolean personIsActionManager(Person person, Institution institution) {
        return actionManagerRepository.findByPersonAndInstitution(person, institution).isPresent();
    }

    public boolean personIsInstitutionManagerOrActionManager(Person person, Institution institution) {
        return personIsInstitutionManager(person, institution) || personIsActionManager(person, institution);
    }

}
