package fr.siamois.services;

import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.SpatialUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordingUnitServiceTest {

    @Mock
    private RecordingUnitRepository recordingUnitRepository;

    @InjectMocks
    private RecordingUnitService recordingUnitService;

    SpatialUnit spatialUnit1 ;
    RecordingUnit recordingUnit1 ;
    RecordingUnit recordingUnit2 ;

    @BeforeEach
    void setUp() {
        spatialUnit1 = new SpatialUnit();
        recordingUnit1 = new RecordingUnit();
        recordingUnit2 = new RecordingUnit();
        spatialUnit1.setId(1L);
        recordingUnit1.setId(1L);
        recordingUnit2.setId(2L);
        when(recordingUnitRepository.findAllBySpatialUnitId(spatialUnit1.getId())).thenReturn(List.of(recordingUnit1, recordingUnit2));
        //lenient().when(spatialUnitRepository.findAllChildOfSpatialUnit(spatialUnit1.getId())).thenReturn(List.of(spatialUnit2));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllBySpatialUnitId_Success() {

        // Act
        List<RecordingUnit> actualResult = recordingUnitService.findAllBySpatialUnitId(spatialUnit1);

        // Assert
        assertEquals(List.of(recordingUnit1,recordingUnit2), actualResult);
    }

    @Test
    void findAllBySpatialUnitId_Exception() {

        // Arrange
        when(recordingUnitRepository.findAllBySpatialUnitId(spatialUnit1.getId())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> recordingUnitService.findAllBySpatialUnitId(spatialUnit1)
        );

        assertEquals("Database error", exception.getMessage());

    }
}