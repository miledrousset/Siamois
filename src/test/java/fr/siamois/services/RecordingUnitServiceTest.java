package fr.siamois.services;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.vocabulary.ConceptService;
import fr.siamois.services.vocabulary.FieldService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.webservices.server.AutoConfigureMockWebServiceClient;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RecordingUnitServiceTest {

    @Mock
    private RecordingUnitRepository recordingUnitRepository;
    @Mock
    private ArkServerRepository arkServerRepository;
    @Mock
    private FieldService fieldService;
    @Mock
    private ConceptService conceptService;

    @InjectMocks
    private RecordingUnitService recordingUnitService;

    SpatialUnit spatialUnit1;
    RecordingUnit recordingUnit1;
    RecordingUnit recordingUnit2;
    Ark newArk;
    ArkServer mockArkServer;
    Vocabulary vocabulary;
    ConceptFieldDTO dto;
    Concept concept;


    RecordingUnit recordingUnitToSave;

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

        Institution parentInstitution = new Institution();
        parentInstitution.setIdentifier("MOM");
        ActionUnit actionUnit = new ActionUnit();
        actionUnit.setIdentifier("2025");
        actionUnit.setMinRecordingUnitCode(5);
        actionUnit.setId(1L);
        actionUnit.setMaxRecordingUnitCode(5);
        actionUnit.setCreatedByInstitution(parentInstitution);
        recordingUnitToSave = new RecordingUnit();
        recordingUnitToSave.setActionUnit(actionUnit);
        recordingUnitToSave.setCreatedByInstitution(parentInstitution);


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
        assertEquals(List.of(recordingUnit1, recordingUnit2), actualResult);
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

        when(recordingUnitRepository.findById(recordingUnit1.getId())).thenReturn(Optional.empty());


        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> recordingUnitService.findById(recordingUnit1.getId())
        );

        assertEquals("RecordingUnit not found with ID: 1", exception.getMessage());

    }

    @Test
    void save_Success() {

        when(arkServerRepository.findLocalServer()).thenReturn(Optional.of(new ArkServer()));
        Concept c = new Concept();
        c.setLabel("Unité strati");
        when(conceptService.saveOrGetConcept(c)).thenReturn(c);

        when(recordingUnitRepository.findMaxUsedIdentifierByAction(anyLong())).thenReturn(null);

        when(recordingUnitRepository.save(
                any(RecordingUnit.class)
        )).thenReturn(recordingUnitToSave);

        RecordingUnit result = recordingUnitService.save(recordingUnitToSave,c);

        // assert
        assertNotNull(result);
        assertEquals("MOM-2025-5", result.getFullIdentifier());


    }

    @Test
    void save_Failure_MaxNbOfRecordingsReached() {

        when(arkServerRepository.findLocalServer()).thenReturn(Optional.of(new ArkServer()));
        Concept c = new Concept();
        c.setLabel("Unité strati");

        when(recordingUnitRepository.findMaxUsedIdentifierByAction(anyLong())).thenReturn(5);

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> recordingUnitService.save(recordingUnitToSave,c)
        );

        assertEquals("Max recording unit code reached; Please ask administrator to increase the range", exception.getMessage());


    }

}