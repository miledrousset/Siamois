package fr.siamois.bean.home;

import fr.siamois.bean.SessionSettings;
import fr.siamois.models.Institution;
import fr.siamois.models.auth.Person;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.services.SpatialUnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class HomeBeanTest {

    @Mock
    private SpatialUnitService spatialUnitService;  // Mock the SpatialUnitService
    @Mock
    private SessionSettings sessionSettings;

    @InjectMocks
    private HomeBean homeBean;  // HomeBean under test

    private SpatialUnit spatialUnit1;
    private SpatialUnit spatialUnit2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize the mocks
        // Initialize sample SpatialUnit objects for testing
        spatialUnit1 = new SpatialUnit();
        spatialUnit1.setId(1L);
        spatialUnit2 = new SpatialUnit();
        spatialUnit2.setId(2L);

        Institution institution = new Institution();
        institution.setId(1L);
        institution.setIdentifier("test.code");
        institution.setName("test.name");

        when(sessionSettings.getSelectedInstitution()).thenReturn(institution);

        Person person = new Person();
        person.setId(13L);
        person.setUsername("test.username");

        when(sessionSettings.getAuthenticatedUser()).thenReturn(person);
    }

    @Test
    void testInit_Success() {
        // Given: mock the service to return a list of spatial units
        when(spatialUnitService.findAllWithoutParentsOfInstitution(any(Institution.class))).thenReturn(List.of(spatialUnit1, spatialUnit2));

        // When: call the @PostConstruct method (implicitly triggered during bean initialization)
        homeBean.init();  // The init() method gets invoked automatically by Spring

        // Then: verify that the spatialUnitList is populated correctly
        assertNotNull(homeBean.getSpatialUnitList());  // List should not be null
        assertEquals(2, homeBean.getSpatialUnitList().size());  // The list should contain 2 elements
        assertTrue(homeBean.getSpatialUnitList().contains(spatialUnit1));  // The list should contain spatialUnit1
        assertTrue(homeBean.getSpatialUnitList().contains(spatialUnit2));  // The list should contain spatialUnit2
    }

    @Test
    void testInit_Exception() {
        // Given: mock the service to throw an error
        when(spatialUnitService.findAllWithoutParentsOfInstitution(any(Institution.class))).thenReturn(List.of());

        // When: call the @PostConstruct method (implicitly triggered during bean initialization)
        homeBean.init();

        // Then: verify that the spatialUnitList is populated correctly
        assertNotNull(homeBean.getSpatialUnitList());  // List should not be null
    }
}
