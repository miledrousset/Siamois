package fr.siamois.domain.services;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.institution.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.institution.InstitutionAlreadyExistException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.models.team.ActionManagerRelation;
import fr.siamois.domain.models.team.TeamMemberRelation;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.database.repositories.institution.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.settings.InstitutionSettingsRepository;
import fr.siamois.infrastructure.database.repositories.team.ActionManagerRepository;
import fr.siamois.infrastructure.database.repositories.team.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {

    @Mock
    private InstitutionRepository institutionRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private InstitutionSettingsRepository institutionSettingsRepository;
    @Mock
    private ActionManagerRepository actionManagerRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private InstitutionService institutionService;

    private Institution institution1, institution2;
    private Person manager;
    private ActionUnit actionUnit;

    @BeforeEach
    void setUp() {
        manager = new Person();
        manager.setId(1L);
        manager.setUsername("Username");

        institution1 = new Institution();
        institution1.setId(-1L);
        institution1.setName("First name");
        institution1.setIdentifier("123456");
        institution1.getManagers().add(manager);

        institution2 = new Institution();
        institution2.setId(-2L);
        institution2.setName("Second name");
        institution2.setIdentifier("0987654");

        actionUnit = new ActionUnit();
        actionUnit.setId(1L);
        actionUnit.setAuthor(manager);
        actionUnit.setCreatedByInstitution(institution1);
    }

    @Test
    void findAll() {
        when(institutionRepository.findAll()).thenReturn(List.of(institution1, institution2));

        Set<Institution> result = institutionService.findAll();

        assertThat(result).containsExactlyInAnyOrder(institution1, institution2);
    }

    @Test
    void createInstitution_throwsInstitutionAlreadyExist() {
        when(institutionRepository.findInstitutionByIdentifier("123456")).thenReturn(Optional.of(institution1));

        assertThrows(InstitutionAlreadyExistException.class, () -> institutionService.createInstitution(institution1));
    }

    @Test
    void createInstitution_throwsFailedInstitutionSaveException() {
        when(institutionRepository.save(institution1)).thenThrow(new RuntimeException("Error while saving institution"));

        assertThrows(FailedInstitutionSaveException.class, () -> institutionService.createInstitution(institution1));
    }

    @Test
    void createInstitution() throws InstitutionAlreadyExistException, FailedInstitutionSaveException {
        institutionService.createInstitution(institution1);

        verify(institutionRepository, times(1)).save(institution1);
    }

    @Test
    void addUserToInstitution() throws FailedInstitutionSaveException {
        Concept realRole = new Concept();
        realRole.setId(1L);

        institutionService.addUserToInstitution(manager, institution1, realRole);

        verify(personRepository, times(1)).addPersonToInstitution(manager.getId(), institution1.getId(), realRole.getId());
    }

    @Test
    void addUserToInstitution_throwsFailedInstitutionSaveException() {
        Concept realRole = new Concept();
        realRole.setId(1L);

        doThrow(new RuntimeException("Error while adding person to institution"))
                .when(personRepository)
                .addPersonToInstitution(manager.getId(), institution1.getId(), realRole.getId());

        assertThrows(FailedInstitutionSaveException.class, () ->
                institutionService.addUserToInstitution(manager, institution1, realRole));
    }

    @Test
    void createOrGetSettingsOf_shouldReturnSettings_whenSet() {
        Institution institution = new Institution();
        institution.setId(1L);

        InstitutionSettings settings = new InstitutionSettings();
        settings.setInstitution(institution);
        settings.setArkNaan("66666");

        when(institutionSettingsRepository.findById(institution.getId())).thenReturn(Optional.of(settings));

        InstitutionSettings result = institutionService.createOrGetSettingsOf(institution);

        assertThat(result).isEqualTo(settings);
    }

    @Test
    void createOrGetSettingsOf_shouldReturnEmptySettings_whenNotSet() {
        Institution institution = new Institution();
        institution.setId(1L);

        when(institutionSettingsRepository.findById(institution.getId())).thenReturn(Optional.empty());
        when(institutionRepository.findById(institution.getId())).thenReturn(Optional.of(institution));
        when(institutionSettingsRepository.save(any(InstitutionSettings.class)))
                .then(invocation -> invocation.getArgument(0, InstitutionSettings.class));

        InstitutionSettings result = institutionService.createOrGetSettingsOf(institution);

        assertThat(result.getInstitution()).isEqualTo(institution);
    }

    @Test
    void isManagerOf_whenIsOwnerOfInstitution_shouldReturnTrue() {
        Person person = new Person();
        person.setUsername("username");
        person.setEmail("test@example.com");
        person.setPassword("password123");
        person.setId(12L);

        Institution institution = new Institution();
        institution.setId(2L);
        institution.setName("institution");
        institution.getManagers().add(person);

        boolean result = institutionService.isManagerOf(institution, person);

        assertThat(result).isTrue();
    }

    @Test
    void update() {
        Institution institution = new Institution();
        institution.setId(1L);
        institution.setName("Updated Institution");

        when(institutionRepository.save(institution)).thenReturn(institution);

        Institution result = institutionService.update(institution);

        assertThat(result).isEqualTo(institution);
        verify(institutionRepository, times(1)).save(institution);
    }

    @Test
    void findInstitutionsOfPerson_shouldReturnAllInstitutions() {
        Person person = new Person();
        person.setId(1L);

        when(institutionRepository.findAllAsMember(person.getId())).thenReturn(Set.of(institution1));
        when(institutionRepository.findAllAsActionManager(person.getId())).thenReturn(Set.of(institution2));
        when(institutionRepository.findAllAsInstitutionManager(person.getId())).thenReturn(Set.of(institution1, institution2));

        Set<Institution> result = institutionService.findInstitutionsOfPerson(person);

        assertThat(result).containsExactlyInAnyOrder(institution1, institution2);
    }

    @Test
    void findRelationsOf_shouldReturnAllRelations() {
        actionUnit = new ActionUnit();
        actionUnit.setId(1L);
        actionUnit.setAuthor(manager);

        Institution institution = new Institution();
        institution.setId(1L);
        institution.setName("Updated Institution");
        institution.setIdentifier("123456");

        actionUnit.setCreatedByInstitution(institution);

        TeamMemberRelation relation = new TeamMemberRelation(actionUnit, manager);

        when(teamMemberRepository.findAllByActionUnit(actionUnit)).thenReturn(new HashSet<>(Set.of(relation)));

        Set<TeamMemberRelation> result = institutionService.findRelationsOf(actionUnit);

        assertThat(result).contains(relation);
    }

    @Test
    void findMembersOf_shouldReturnAllMembers() {
        actionUnit = new ActionUnit();
        actionUnit.setId(1L);
        actionUnit.setAuthor(manager);

        Institution institution = new Institution();
        institution.setId(1L);
        institution.setName("Updated Institution");

        actionUnit.setCreatedByInstitution(institution);

        TeamMemberRelation relation = new TeamMemberRelation(actionUnit, manager);

        when(teamMemberRepository.findAllByActionUnit(actionUnit)).thenReturn(new HashSet<>(Set.of(relation)));

        Set<Person> result = institutionService.findMembersOf(actionUnit);

        assertThat(result).containsExactlyInAnyOrder(manager);
    }

    @Test
    void addToManagers_shouldAddManagerAndReturnTrue() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(2L);

        when(institutionRepository.save(institution)).thenReturn(institution);

        boolean result = institutionService.addToManagers(institution, person);

        assertThat(result).isTrue();
        assertThat(institution.getManagers()).contains(person);
        verify(institutionRepository, times(1)).save(institution);
    }

    @Test
    void addToManagers_shouldNotAddManagerIfAlreadyExists() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(2L);
        institution.getManagers().add(person);

        boolean result = institutionService.addToManagers(institution, person);

        assertThat(result).isFalse();
        verify(institutionRepository, times(1)).save(institution);
    }

    @Test
    void countMembersInInstitution_shouldReturnCorrectCount() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person1 = new Person();
        person1.setId(1L);

        Person person2 = new Person();
        person2.setId(2L);

        when(teamMemberRepository.findAllByInstitution(institution.getId()))
                .thenReturn(Set.of(new TeamMemberRelation(actionUnit, person1), new TeamMemberRelation(actionUnit, person2)));
        when(actionManagerRepository.findAllByInstitution(institution))
                .thenReturn(Set.of(new ActionManagerRelation(institution, person1)));

        long result = institutionService.countMembersInInstitution(institution);

        assertThat(result).isEqualTo(2);
    }

    @Test
    void personIsInInstitution_shouldReturnTrueIfActionManager() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(actionManagerRepository.findByPersonAndInstitution(person, institution))
                .thenReturn(Optional.of(new ActionManagerRelation(institution, person)));

        boolean result = institutionService.personIsInInstitution(person, institution);

        assertThat(result).isTrue();
    }

    @Test
    void personIsInInstitution_shouldReturnTrueIfTeamMember() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(actionManagerRepository.findByPersonAndInstitution(person, institution)).thenReturn(Optional.empty());
        when(teamMemberRepository.personIsInInstitution(person.getId(), institution.getId())).thenReturn(true);

        boolean result = institutionService.personIsInInstitution(person, institution);

        assertThat(result).isTrue();
    }

    @Test
    void personIsInInstitution_shouldReturnFalseIfNotInInstitution() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(actionManagerRepository.findByPersonAndInstitution(person, institution)).thenReturn(Optional.empty());
        when(teamMemberRepository.personIsInInstitution(person.getId(), institution.getId())).thenReturn(false);

        boolean result = institutionService.personIsInInstitution(person, institution);

        assertThat(result).isFalse();
    }

    @Test
    void findAllActionManagersOf_shouldReturnAllActionManagers() {
        Institution institution = new Institution();
        institution.setId(1L);

        ActionManagerRelation relation = new ActionManagerRelation(institution, manager);

        when(actionManagerRepository.findAllByInstitution(institution)).thenReturn(Set.of(relation));

        Set<ActionManagerRelation> result = institutionService.findAllActionManagersOf(institution);

        assertThat(result).containsExactlyInAnyOrder(relation);
    }

    @Test
    void personIsInstitutionManager_shouldReturnTrueIfManager() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(institutionRepository.personIsInstitutionManager(institution.getId(), person.getId())).thenReturn(true);

        boolean result = institutionService.personIsInstitutionManager(person, institution);

        assertThat(result).isTrue();
    }

    @Test
    void personIsInstitutionManager_shouldReturnFalseIfNotManager() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(institutionRepository.personIsInstitutionManager(institution.getId(), person.getId())).thenReturn(false);

        boolean result = institutionService.personIsInstitutionManager(person, institution);

        assertThat(result).isFalse();
    }

    @Test
    void personIsActionManager_shouldReturnTrueIfActionManager() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(actionManagerRepository.findByPersonAndInstitution(person, institution))
                .thenReturn(Optional.of(new ActionManagerRelation(institution, person)));

        boolean result = institutionService.personIsActionManager(person, institution);

        assertThat(result).isTrue();
    }

    @Test
    void personIsActionManager_shouldReturnFalseIfNotActionManager() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(actionManagerRepository.findByPersonAndInstitution(person, institution)).thenReturn(Optional.empty());

        boolean result = institutionService.personIsActionManager(person, institution);

        assertThat(result).isFalse();
    }

    @Test
    void personIsInstitutionManagerOrActionManager_shouldReturnTrueIfManager() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(institutionRepository.personIsInstitutionManager(institution.getId(), person.getId())).thenReturn(true);

        boolean result = institutionService.personIsInstitutionManagerOrActionManager(person, institution);

        assertThat(result).isTrue();
    }

    @Test
    void personIsInstitutionManagerOrActionManager_shouldReturnTrueIfActionManager() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(institutionRepository.personIsInstitutionManager(institution.getId(), person.getId())).thenReturn(false);
        when(actionManagerRepository.findByPersonAndInstitution(person, institution))
                .thenReturn(Optional.of(new ActionManagerRelation(institution, person)));

        boolean result = institutionService.personIsInstitutionManagerOrActionManager(person, institution);

        assertThat(result).isTrue();
    }

    @Test
    void personIsInstitutionManagerOrActionManager_shouldReturnFalseIfNeither() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(institutionRepository.personIsInstitutionManager(institution.getId(), person.getId())).thenReturn(false);
        when(actionManagerRepository.findByPersonAndInstitution(person, institution)).thenReturn(Optional.empty());

        boolean result = institutionService.personIsInstitutionManagerOrActionManager(person, institution);

        assertThat(result).isFalse();
    }

    @Test
    void addPersonToActionManager_shouldAddManagerAndReturnTrue() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(actionManagerRepository.findByPersonAndInstitution(person, institution)).thenReturn(Optional.empty());

        boolean result = institutionService.addPersonToActionManager(institution, person);

        assertThat(result).isTrue();
        verify(actionManagerRepository, times(1)).save(any(ActionManagerRelation.class));
    }

    @Test
    void addPersonToActionManager_shouldNotAddManagerIfAlreadyExists() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person person = new Person();
        person.setId(1L);

        when(actionManagerRepository.findByPersonAndInstitution(person, institution))
                .thenReturn(Optional.of(new ActionManagerRelation(institution, person)));

        boolean result = institutionService.addPersonToActionManager(institution, person);

        assertThat(result).isFalse();
        verify(actionManagerRepository, never()).save(any(ActionManagerRelation.class));
    }

    @Test
    void addPersonToActionUnit_shouldAddMemberAndReturnTrue() {
        actionUnit = new ActionUnit();
        actionUnit.setId(1L);

        Person person = new Person();
        person.setId(1L);

        Concept role = new Concept();
        role.setId(1L);

        when(teamMemberRepository.findByActionUnitAndPerson(actionUnit, person)).thenReturn(Optional.empty());

        boolean result = institutionService.addPersonToActionUnit(actionUnit, person, role);

        assertThat(result).isTrue();
        verify(teamMemberRepository, times(1)).save(any(TeamMemberRelation.class));
    }

    @Test
    void addPersonToActionUnit_shouldNotAddMemberIfAlreadyExists() {
        actionUnit = new ActionUnit();
        actionUnit.setId(1L);

        Person person = new Person();
        person.setId(1L);

        Concept role = new Concept();
        role.setId(1L);

        when(teamMemberRepository.findByActionUnitAndPerson(actionUnit, person))
                .thenReturn(Optional.of(new TeamMemberRelation(actionUnit, person)));

        boolean result = institutionService.addPersonToActionUnit(actionUnit, person, role);

        assertThat(result).isFalse();
        verify(teamMemberRepository, never()).save(any(TeamMemberRelation.class));
    }

    @Test
    void findManagersOf_shouldReturnAllManagers() {
        Institution institution = new Institution();
        institution.setId(1L);

        Person manager1 = new Person();
        manager1.setId(1L);

        Person manager2 = new Person();
        manager2.setId(2L);

        institution.getManagers().add(manager1);
        institution.getManagers().add(manager2);

        Set<Person> result = institutionService.findManagersOf(institution);

        assertThat(result).containsExactlyInAnyOrder(manager1, manager2);
    }

    @Test
    void findById_success() {
        Institution institution = new Institution();
        institution.setId(1L);
        when(institutionRepository.findById(1L)).thenReturn(Optional.of(institution));
        Institution res = institutionService.findById(1L);
        assertThat(institution).isNotNull();
        assertThat(res.getId()).isEqualTo(1L);
    }

    @Test
    void findById_null() {
        when(institutionRepository.findById(1L)).thenReturn(Optional.empty());
        Institution institution = institutionService.findById(1L);
        assertThat(institution).isNull();
    }

}