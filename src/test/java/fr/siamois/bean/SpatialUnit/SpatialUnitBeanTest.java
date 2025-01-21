package fr.siamois.bean.SpatialUnit;

import fr.siamois.models.ActionUnit;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.HistoryService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.SpatialUnitService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SpatialUnitBeanTest {

    @Mock private SpatialUnitService spatialUnitService;  // Mock the SpatialUnitService
    @Mock private ActionUnitService actionUnitService;  // Mock the SpatialUnitService
    @Mock private RecordingUnitService recordingUnitService;  // Mock the SpatialUnitService
    @Mock private HistoryService historyService;

    @InjectMocks
    private SpatialUnitBean spatialUnitBean;  // HomeBean under test

    private SpatialUnit spatialUnit1;
    private SpatialUnit spatialUnit2;
    private RecordingUnit recordingUnit1;
    private RecordingUnit recordingUnit2;
    private ActionUnit actionUnit1;
    private ActionUnit actionUnit2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize the mocks
        // Initialize sample SpatialUnit objects for testing
        spatialUnit1 = new SpatialUnit();
        spatialUnit1.setId(1L);
        spatialUnit2 = new SpatialUnit();
        spatialUnit2.setId(2L);

        // Initialize sample ActionUnit objects for testing
        actionUnit1 = new ActionUnit();
        actionUnit1.setId(1L);
        actionUnit2 = new ActionUnit();
        actionUnit2.setId(2L);

        // Initialize sample RecordingUnit objects for testing
        recordingUnit1 = new RecordingUnit();
        recordingUnit1.setId(1L);
        recordingUnit2 = new RecordingUnit();
        recordingUnit2.setId(2L);

        // Initialize the bean
        spatialUnitBean.setId(1L);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testInit_Success() {

        // Given: mock the services
        when(spatialUnitService.findById(1)).thenReturn(spatialUnit1);
        when(spatialUnitService.findAllChildOfSpatialUnit(spatialUnit1)).thenReturn(List.of(spatialUnit2));
        when(actionUnitService.findAllBySpatialUnitId(spatialUnit1)).thenReturn(List.of(actionUnit1, actionUnit2));
        when(recordingUnitService.findAllBySpatialUnit(spatialUnit1)).thenReturn(List.of(recordingUnit1, recordingUnit2));

        // When: call the @PostConstruct method (implicitly triggered during bean initialization)
        spatialUnitBean.init();

        // Then: verify that the bean is populated properly
        // The selected spatial unit
        assertNotNull(spatialUnitBean.getSpatialUnit());  // List should not be null
        assertEquals(spatialUnit1, spatialUnitBean.getSpatialUnit());

        // List of action units
        assertNotNull(spatialUnitBean.getActionUnitList());  // List should not be null
        assertEquals(2, spatialUnitBean.getActionUnitList().size());  // The list should contain 2 elements
        assertTrue(spatialUnitBean.getActionUnitList().contains(actionUnit1));  // The list should contain actionUnit1
        assertTrue(spatialUnitBean.getActionUnitList().contains(actionUnit2));  // The list should contain actionUnit2

        // List of recording units
        assertNotNull(spatialUnitBean.getRecordingUnitList());  // List should not be null
        assertEquals(2, spatialUnitBean.getRecordingUnitList().size());  // The list should contain 2 elements
        assertTrue(spatialUnitBean.getRecordingUnitList().contains(recordingUnit1));  // The list should contain recordingUnit1
        assertTrue(spatialUnitBean.getRecordingUnitList().contains(recordingUnit2));  // The list should contain recordingUnit2

        // List of spatial units
        assertNotNull(spatialUnitBean.getSpatialUnitList());  // List should not be null
        assertEquals(1, spatialUnitBean.getSpatialUnitList().size());  // The list should contain 1 element
        assertTrue(spatialUnitBean.getSpatialUnitList().contains(spatialUnit2));  // The list should contain spatialUnit2
    }

    @Test
    void testInit_FailToGetSelectedSpatialUnit() {

        // Given: mock the services
        when(spatialUnitService.findById(1)).thenThrow(new RuntimeException("Exception"));

        // When: call the @PostConstruct method (implicitly triggered during bean initialization)
        spatialUnitBean.init();

        // Then: verify that the bean is populated properly
        assertNull(spatialUnitBean.getSpatialUnit());
        assertEquals("Failed to load spatial unit: Exception", spatialUnitBean.getSpatialUnitErrorMessage());
    }

    @Test
    void testInit_FailToGetChildrenOfSelectedSpatialUnit() {

        // Given: mock the services
        when(spatialUnitService.findById(1)).thenReturn(spatialUnit1);
        when(spatialUnitService.findAllChildOfSpatialUnit(spatialUnit1)).thenThrow(new RuntimeException("Exception"));
        when(recordingUnitService.findAllBySpatialUnit(spatialUnit1)).thenThrow(new RuntimeException("Exception"));
        when(actionUnitService.findAllBySpatialUnitId(spatialUnit1)).thenThrow(new RuntimeException("Exception"));

        // When: call the @PostConstruct method (implicitly triggered during bean initialization)
        spatialUnitBean.init();

        // Then: verify that the bean is populated properly
        // The selected spatial unit
        assertNotNull(spatialUnitBean.getSpatialUnit());  // List should not be null
        assertEquals(spatialUnit1, spatialUnitBean.getSpatialUnit());

        // Error messages
        assertNull(spatialUnitBean.getSpatialUnitList());
        assertEquals("Unable to load spatial units: Exception", spatialUnitBean.getSpatialUnitListErrorMessage());
        assertNull(spatialUnitBean.getActionUnitList());
        assertEquals("Unable to load action units: Exception", spatialUnitBean.getActionUnitListErrorMessage());
        assertNull(spatialUnitBean.getRecordingUnitList());
        assertEquals("Unable to load recording units: Exception", spatialUnitBean.getRecordingUnitListErrorMessage());
    }





}