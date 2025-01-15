package fr.siamois.services;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.vocabulary.FieldService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RecordingUnitServiceTest {

    @Mock
    private RecordingUnitRepository recordingUnitRepository;

    @Mock
    private ArkServerRepository arkServerRepository;

    @Mock
    private FieldService fieldService;

    @InjectMocks
    private RecordingUnitService recordingUnitService;

    SpatialUnit spatialUnit1 ;
    RecordingUnit recordingUnit1 ;
    RecordingUnit recordingUnit2 ;
    Ark newArk;
    ArkServer mockArkServer;
    Vocabulary vocabulary;
    ConceptFieldDTO dto;
    Concept concept;

    @BeforeEach
    void setUp() {
        spatialUnit1 = new SpatialUnit();
        recordingUnit1 = new RecordingUnit();
        recordingUnit2 = new RecordingUnit();
        spatialUnit1.setId(1L);
        recordingUnit1.setId(1L);
        recordingUnit2.setId(2L);
        concept = new Concept();
        newArk = new Ark();
        mockArkServer = new ArkServer();
        mockArkServer.setServerArkUri("http://localhost:8099/siamois");
        vocabulary = new Vocabulary();
        dto = new ConceptFieldDTO();


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


        when(recordingUnitRepository.save(any(RecordingUnit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Return the same object


        when(arkServerRepository.findLocalServer()).thenReturn(Optional.ofNullable(mockArkServer));

        when(fieldService.saveOrGetConceptFromDto(vocabulary, dto)).thenReturn(concept);

        // Act
        RecordingUnit result = recordingUnitService.save(recordingUnit1, vocabulary, dto);

        // Assert
        assertNotNull(result.getArk());
        assertEquals(mockArkServer, result.getArk().getArkServer());
        assertNotNull(result.getArk().getArkId());
        verify(recordingUnitRepository, times(1)).save(any(RecordingUnit.class));
    }

    @Test
    void save_Exception() {


        when(arkServerRepository.findLocalServer()).thenReturn(Optional.ofNullable(mockArkServer));

        when(fieldService.saveOrGetConceptFromDto(vocabulary, dto)).thenReturn(concept);

        when(recordingUnitRepository.save(any(RecordingUnit.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                FailedRecordingUnitSaveException.class,
                () -> recordingUnitService.save(recordingUnit1, vocabulary, dto)
        );

        assertEquals("Database error", exception.getMessage());
    }
}