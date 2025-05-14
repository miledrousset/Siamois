package fr.siamois.infrastructure.database.initializer;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.services.person.TeamService;
import fr.siamois.infrastructure.database.repositories.institution.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminInitializerTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private TeamService teamService;

    private AdminInitializer adminInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminInitializer = new AdminInitializer(passwordEncoder,
                personRepository,
                institutionRepository,
                applicationContext,
                teamService);
        adminInitializer.setAdminUsername("admin");
        adminInitializer.setAdminPassword("admin");
        adminInitializer.setAdminEmail("admin@example.com");
    }

    @Test
    void initializeAdmin_shouldCreateAdminWhenNoAdminExists() throws DatabaseDataInitException {
        when(personRepository.findAllSuperAdmin()).thenReturn(List.of());
        when(passwordEncoder.encode("admin")).thenReturn("encodedPassword");
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adminInitializer.initialize();

        assertNotNull(adminInitializer.getCreatedAdmin());
        assertEquals("admin", adminInitializer.getCreatedAdmin().getUsername());
        assertEquals("encodedPassword", adminInitializer.getCreatedAdmin().getPassword());
    }

    @Test
    void initializeAdmin_shouldNotCreateAdminWhenAdminExists() throws DatabaseDataInitException {
        Person existingAdmin = new Person();
        existingAdmin.setUsername("admin");
        when(personRepository.findAllSuperAdmin()).thenReturn(List.of(existingAdmin));

        adminInitializer.initialize();

        assertEquals(existingAdmin, adminInitializer.getCreatedAdmin());
    }

    @Test
    void initializeAdmin_shouldThrowExceptionWhenAdminUsernameExistsButIsNotAdmin() {
        when(personRepository.findAllSuperAdmin()).thenReturn(List.of());
        when(personRepository.save(any(Person.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DatabaseDataInitException.class, () -> adminInitializer.initializeAdmin());
    }

    @Test
    void initializeAdminOrganization_shouldCreateInstitutionWhenNoInstitutionExists() {
        when(institutionRepository.findInstitutionByIdentifier("SIAMOIS")).thenReturn(Optional.empty());
        when(institutionRepository.save(any(Institution.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adminInitializer.setCreatedAdmin(new Person());
        adminInitializer.initializeAdminOrganization();

        verify(institutionRepository, times(1)).save(any(Institution.class));
    }

    @Test
    void initializeAdminOrganization_shouldNotCreateInstitutionWhenInstitutionExists() {
        Person oldAdmin = new Person();
        oldAdmin.setId(12L);

        Institution existingInstitution = new Institution();
        existingInstitution.getManagers().add(oldAdmin);
        when(institutionRepository.findInstitutionByIdentifier("SIAMOIS")).thenReturn(Optional.of(existingInstitution));

        adminInitializer.setCreatedAdmin(oldAdmin);
        adminInitializer.initializeAdminOrganization();

        verify(institutionRepository, never()).save(any(Institution.class));
    }

    @Test
    void initializeAdminOrganization_shouldAddSuperAdminAsManager_whenCreatedAdminIsNotManagerAndInitializationExist() {
        Person otherAdmin = new Person();
        otherAdmin.setId(12L);

        Person createdAdmin = new Person();
        createdAdmin.setId(13L);

        Institution existingInstitution = new Institution();
        existingInstitution.setId(14L);
        existingInstitution.setManagers(new HashSet<>());
        existingInstitution.getManagers().add(otherAdmin);

        when(institutionRepository.findInstitutionByIdentifier("SIAMOIS")).thenReturn(Optional.of(existingInstitution));

        adminInitializer.setCreatedAdmin(createdAdmin);
        adminInitializer.initializeAdminOrganization();

        assertThat(existingInstitution.getManagers()).contains(createdAdmin);
        verify(institutionRepository, times(1)).save(any(Institution.class));
    }
}