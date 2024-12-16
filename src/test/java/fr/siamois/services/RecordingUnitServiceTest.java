package fr.siamois.services;

import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.SpatialUnit;
<<<<<<< HEAD

import fr.siamois.models.ark.Ark;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.models.exceptions.field.FailedFieldSaveException;
=======
>>>>>>> e1e6b54c1d3380614db8290c25919490a071e29f
import fr.siamois.models.recordingunit.RecordingUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.webservices.server.AutoConfigureMockWebServiceClient;

import java.util.List;
import java.util.Optional;

<<<<<<< HEAD

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
=======
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
>>>>>>> e1e6b54c1d3380614db8290c25919490a071e29f

@ExtendWith(MockitoExtension.class)
class RecordingUnitServiceTest {

    @Mock
    private RecordingUnitRepository recordingUnitRepository;

    @Mock
    private ArkServerRepository arkServerRepository;

    @InjectMocks
    private RecordingUnitService recordingUnitService;

    SpatialUnit spatialUnit1 ;
    RecordingUnit recordingUnit1 ;
    RecordingUnit recordingUnit2 ;
    Ark newArk;
    ArkServer mockArkServer;

    @BeforeEach
    void setUp() {
        spatialUnit1 = new SpatialUnit();
        recordingUnit1 = new RecordingUnit();
        recordingUnit2 = new RecordingUnit();
        spatialUnit1.setId(1L);
        recordingUnit1.setId(1L);
        recordingUnit2.setId(2L);
        newArk = new Ark();
        mockArkServer = new ArkServer();
        mockArkServer.setServerArkUri("http://localhost:8099/siamois");


    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllBySpatialUnitId_Success() {

        when(recordingUnitRepository.findAllBySpatialUnitId(spatialUnit1.getId())).thenReturn(List.of(recordingUnit1, recordingUnit2));

        // Act
        List<RecordingUnit> actualResult = recordingUnitService.findAllBySpatialUnit(spatialUnit1);

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
                () -> recordingUnitService.findAllBySpatialUnit(spatialUnit1)
        );

        assertEquals("Database error", exception.getMessage());

    }

    @Test
    void findById_success() {

        when(recordingUnitRepository.findById(recordingUnit1.getId())).thenReturn(Optional.ofNullable(recordingUnit1));

        // act
        RecordingUnit actualResult = recordingUnitService.findById(recordingUnit1.getId());

        // assert
        assertEquals(recordingUnit1, actualResult);
    }

    @Test
    void findById_Exception() {

        when(recordingUnitRepository.findById(recordingUnit1.getId())).thenReturn(Optional.ofNullable(null));



        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> recordingUnitService.findById(recordingUnit1.getId())
        );

        assertEquals("RecordingUnit not found with ID: 1", exception.getMessage());

    }

    @Test
    void save_success() {
        when(arkServerRepository.findArkServerByServerArkUri("http://localhost:8099/siamois"))
                .thenReturn(Optional.of(mockArkServer));

        when(recordingUnitRepository.save(any(RecordingUnit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Return the same object

        // Act
        RecordingUnit result = recordingUnitService.save(recordingUnit1);

        // Assert
        assertNotNull(result.getArk());
        assertEquals(mockArkServer, result.getArk().getArkServer());
        assertNotNull(result.getArk().getArkId());
        verify(arkServerRepository, times(1))
                .findArkServerByServerArkUri("http://localhost:8099/siamois");
        verify(recordingUnitRepository, times(1)).save(any(RecordingUnit.class));
    }

    @Test
    void save_Exception() {
        when(arkServerRepository.findArkServerByServerArkUri("http://localhost:8099/siamois"))
                .thenReturn(Optional.of(mockArkServer));

        when(recordingUnitRepository.save(any(RecordingUnit.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                FailedRecordingUnitSaveException.class,
                () -> recordingUnitService.save(recordingUnit1)
        );

        assertEquals("Database error", exception.getMessage());
    }
}