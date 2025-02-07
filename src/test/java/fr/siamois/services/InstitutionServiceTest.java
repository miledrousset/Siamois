package fr.siamois.services;

import fr.siamois.infrastructure.repositories.InstitutionRepository;
import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.FailedInstitutionSaveException;
import fr.siamois.models.exceptions.InstitutionAlreadyExist;
import fr.siamois.models.vocabulary.Concept;
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

    private InstitutionService institutionService;

    private Institution institution1, institution2;
    private Person manager;

    @BeforeEach
    void setUp() {
        institutionService = new InstitutionService(institutionRepository, personRepository);

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

        assertThrows(InstitutionAlreadyExist.class, () -> institutionService.createInstitution(institution1));
    }

    @Test
    void createInstitution_throwsFailedInstitutionSaveException() {
        when(institutionRepository.save(institution1)).thenThrow(new RuntimeException("Error while saving institution"));

        assertThrows(FailedInstitutionSaveException.class, () -> institutionService.createInstitution(institution1));
    }

    @Test
    void createInstitution() throws InstitutionAlreadyExist, FailedInstitutionSaveException {
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
}