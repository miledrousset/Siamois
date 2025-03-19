package fr.siamois.infrastructure.database;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.infrastructure.database.repositories.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.auth.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

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

    private AdminInitializer adminInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminInitializer = new AdminInitializer(passwordEncoder, personRepository, institutionRepository);
        adminInitializer.setAdminUsername("admin");
        adminInitializer.setAdminPassword("admin");
        adminInitializer.setAdminEmail("admin@example.com");
    }

    @Test
    void initializeAdmin_shouldCreateAdminWhenNoAdminExists() {
        when(personRepository.findAllByIsSuperAdmin(true)).thenReturn(List.of());
        when(passwordEncoder.encode("admin")).thenReturn("encodedPassword");
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = adminInitializer.initializeAdmin();

        assertTrue(result);
        assertNotNull(adminInitializer.getCreatedAdmin());
        assertEquals("admin", adminInitializer.getCreatedAdmin().getUsername());
        assertEquals("encodedPassword", adminInitializer.getCreatedAdmin().getPassword());
    }

    @Test
    void initializeAdmin_shouldNotCreateAdminWhenAdminExists() {
        Person existingAdmin = new Person();
        existingAdmin.setUsername("admin");
        when(personRepository.findAllByIsSuperAdmin(true)).thenReturn(List.of(existingAdmin));

        boolean result = adminInitializer.initializeAdmin();

        assertFalse(result);
        assertEquals(existingAdmin, adminInitializer.getCreatedAdmin());
    }

    @Test
    void initializeAdmin_shouldThrowExceptionWhenAdmiUsernameExistButIsNotAdmin() {
        Person existingAdmin = new Person();
        existingAdmin.setUsername("otherAdmin");
        when(personRepository.findAllByIsSuperAdmin(true)).thenReturn(List.of());
        when(personRepository.save(any(Person.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> adminInitializer.initializeAdmin());
    }

    @Test
    void initializeAdminOrganization_shouldCreateInstitutionWhenNoInstitutionExists() {
        when(institutionRepository.findInstitutionByIdentifier("SIAMOIS")).thenReturn(Optional.empty());
        when(institutionRepository.save(any(Institution.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adminInitializer.setCreatedAdmin(new Person());
        boolean result = adminInitializer.initializeAdminOrganization();

        assertTrue(result);
        verify(institutionRepository, times(1)).save(any(Institution.class));
    }

    @Test
    void initializeAdminOrganization_shouldNotCreateInstitutionWhenInstitutionExists() {
        Person oldAdmin = new Person();
        oldAdmin.setId(12L);

        Institution existingInstitution = new Institution();
        existingInstitution.setManager(oldAdmin);
        when(institutionRepository.findInstitutionByIdentifier("SIAMOIS")).thenReturn(Optional.of(existingInstitution));

        adminInitializer.setCreatedAdmin(oldAdmin);
        boolean result = adminInitializer.initializeAdminOrganization();

        assertFalse(result);
        verify(institutionRepository, never()).save(any(Institution.class));
    }

    @Test
    void initializeAdminOrganization_shouldUpdateManagerWhenInstitutionExistsWithDifferentManager() {
        Person oldAdmin = new Person();
        oldAdmin.setId(12L);

        Institution existingInstitution = new Institution();
        existingInstitution.setManager(oldAdmin);
        when(institutionRepository.findInstitutionByIdentifier("SIAMOIS")).thenReturn(Optional.of(existingInstitution));

        Person newAdmin = new Person();
        newAdmin.setId(5L);
        adminInitializer.setCreatedAdmin(newAdmin);
        boolean result = adminInitializer.initializeAdminOrganization();

        assertFalse(result);
        assertEquals(newAdmin, existingInstitution.getManager());
        verify(institutionRepository, times(1)).save(existingInstitution);
    }
}