package fr.siamois.domain.services;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.institution.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.institution.InstitutionAlreadyExistException;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.database.repositories.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.auth.PersonRepository;
import fr.siamois.infrastructure.database.repositories.settings.InstitutionSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {

    @Mock private InstitutionRepository institutionRepository;
    @Mock private PersonRepository personRepository;
    @Mock private InstitutionSettingsRepository institutionSettingsRepository;

    private InstitutionService institutionService;

    private Institution institution1, institution2;
    private Person manager;

    @BeforeEach
    void setUp() {
        institutionService = new InstitutionService(institutionRepository, personRepository, institutionSettingsRepository);

        manager = new Person();
        manager.setId(1L);
        manager.setUsername("Username");

        institution1 = new Institution();
        institution1.setId(-1L);
        institution1.setName("First name");
        institution1.setIdentifier("123456");
        institution1.setManager(manager);

        institution2 = new Institution();
        institution2.setId(-2L);
        institution2.setName("Second name");
        institution2.setIdentifier("0987654");
    }

    @Test
    void findAll() {
        when(institutionRepository.findAll()).thenReturn(List.of(institution1, institution2));

        List<Institution> result = institutionService.findAll();

        assertThat(result).containsExactlyInAnyOrder(institution1, institution2);
    }

    @Test
    void findInstitutionsOfPerson() {
        when(institutionRepository.findAllManagedBy(1L)).thenReturn(new ArrayList<>(List.of(institution1)));

        List<Institution> result = institutionService.findInstitutionsOfPerson(manager);

        assertThat(result).containsExactlyInAnyOrder(institution1);
    }

    @Test
    void findAllManagers() {
        when(personRepository.findAllInstitutionManagers()).thenReturn(List.of(manager));

        List<Person> result = institutionService.findAllManagers();

        assertThat(result).containsExactlyInAnyOrder(manager);
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
    void findMembersOf() {
        List<Person> result = institutionService.findMembersOf(institution1);

        assertThat(result).containsExactlyInAnyOrder(manager);
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
        when(institutionSettingsRepository.save(any(InstitutionSettings.class)))
                .then(invocation -> invocation.getArgument(0, InstitutionSettings.class));

        InstitutionSettings result = institutionService.createOrGetSettingsOf(institution);

        assertThat(result.getInstitution()).isEqualTo(institution);
    }

    @Test
    void addToManagers_shouldCreateUserLink_whenNotExist() {
        Person person = new Person();
        person.setUsername("username");
        person.setMail("test@example.com");
        person.setPassword("password123");
        person.setId(12L);

        Institution institution = new Institution();
        institution.setId(2L);
        institution.setName("institution");

        when(institutionRepository.personExistInInstitution(12L, 2L)).thenReturn(false);

        institutionService.addToManagers(institution, person);

        verify(institutionRepository, atMostOnce()).addPersonTo(12L, 2L);
        verify(institutionRepository, atMostOnce()).setPersonAsManagerOf(12L, 2L);
    }

    @Test
    void addToManagers_shouldOnlySet_whenExist() {
        Person person = new Person();
        person.setUsername("username");
        person.setMail("test@example.com");
        person.setPassword("password123");
        person.setId(12L);

        Institution institution = new Institution();
        institution.setId(2L);
        institution.setName("institution");

        when(institutionRepository.personExistInInstitution(12L, 2L)).thenReturn(true);

        institutionService.addToManagers(institution, person);

        verify(institutionRepository, never()).addPersonTo(anyLong(), anyLong());
        verify(institutionRepository, atMostOnce()).setPersonAsManagerOf(12L, 2L);
    }

    @Test
    void isManagerOf_whenIsOwnerOfInstitution_shouldReturnTrue() {
        Person person = new Person();
        person.setUsername("username");
        person.setMail("test@example.com");
        person.setPassword("password123");
        person.setId(12L);

        Institution institution = new Institution();
        institution.setId(2L);
        institution.setName("institution");
        institution.setManager(person);

        boolean result = institutionService.isManagerOf(institution, person);

        assertThat(result).isTrue();
    }

    @Test
    void isManagerOf_whenManagerOfTheInstitution_shouldReturnTrue() {
        Person person = new Person();
        person.setId(12L);

        Institution institution = new Institution();
        institution.setId(2L);

        when(institutionRepository.isManagerOf(2L, 12L)).thenReturn(true);

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
    void countMembersInInstitution_withManagerAndNoMembers() {
        Institution institution = new Institution();
        institution.setId(1L);
        institution.setManager(manager);

        when(personRepository.findMembersOfInstitution(institution.getId())).thenReturn(new ArrayList<>());

        long result = institutionService.countMembersInInstitution(institution);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void countMembersInInstitution_withManagerAndMembers() {
        Institution institution = new Institution();
        institution.setId(1L);
        institution.setManager(manager);

        Person member = new Person();
        member.setId(2L);

        List<Person> members = new ArrayList<>();
        members.add(member);

        when(personRepository.findMembersOfInstitution(institution.getId())).thenReturn(members);

        long result = institutionService.countMembersInInstitution(institution);

        assertThat(result).isEqualTo(2);
    }

}