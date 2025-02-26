package fr.siamois.domain.services;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.recordingunit.StratigraphicRelationship;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.recordingunit.StratigraphicRelationshipService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RecordingUnitServiceTest {

    @Mock
    private RecordingUnitRepository recordingUnitRepository;
    @Mock
    private FieldService fieldService;
    @Mock
    private ConceptService conceptService;
    @Mock
    private StratigraphicRelationshipService stratigraphicRelationshipService;

    @InjectMocks
    private RecordingUnitService recordingUnitService;

    SpatialUnit spatialUnit1;
    RecordingUnit recordingUnit1;
    RecordingUnit recordingUnit2;
    Ark newArk;
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

        RecordingUnit anteriorUnit = new RecordingUnit();
        anteriorUnit.setId(1L);
        RecordingUnit synchronousUnit = new RecordingUnit();
        synchronousUnit.setId(2L);
        RecordingUnit posteriorUnit = new RecordingUnit();
        posteriorUnit.setId(3L);

        StratigraphicRelationship antRelationship = new StratigraphicRelationship();
        antRelationship.setUnit1(recordingUnitToSave);
        antRelationship.setUnit2(anteriorUnit);
        antRelationship.setType(StratigraphicRelationshipService.ASYNCHRONOUS);
        StratigraphicRelationship syncRelationship = new StratigraphicRelationship();
        syncRelationship.setUnit1(recordingUnitToSave);
        syncRelationship.setUnit2(synchronousUnit);
        syncRelationship.setType(StratigraphicRelationshipService.SYNCHRONOUS);
        StratigraphicRelationship postRelationship = new StratigraphicRelationship();
        postRelationship.setUnit1(posteriorUnit);
        postRelationship.setUnit2(recordingUnitToSave);
        postRelationship.setType(StratigraphicRelationshipService.ASYNCHRONOUS);

        Concept c = new Concept();
        c.setLabel("Unité strati");
        when(conceptService.saveOrGetConcept(c)).thenReturn(c);

        when(recordingUnitRepository.findMaxUsedIdentifierByAction(anyLong())).thenReturn(null);

        when(recordingUnitRepository.save(
                any(RecordingUnit.class)
        )).thenReturn(recordingUnitToSave);

        when(stratigraphicRelationshipService.saveOrGet(recordingUnitToSave, synchronousUnit, StratigraphicRelationshipService.SYNCHRONOUS))
                .thenReturn(syncRelationship);
        when(stratigraphicRelationshipService.saveOrGet(anteriorUnit, recordingUnitToSave, StratigraphicRelationshipService.ASYNCHRONOUS))
                .thenReturn(antRelationship);
        when(stratigraphicRelationshipService.saveOrGet(recordingUnitToSave, posteriorUnit, StratigraphicRelationshipService.ASYNCHRONOUS))
                .thenReturn(postRelationship);

        RecordingUnit result = recordingUnitService.save(recordingUnitToSave,c,
                List.of(anteriorUnit),
                List.of(synchronousUnit),
                List.of(posteriorUnit)
        );

        // assert
        assertNotNull(result);
        assertEquals("MOM-2025-5", result.getFullIdentifier());

        // Verify that saveOrGet was called the correct number of times with expected arguments
        verify(stratigraphicRelationshipService, times(1))
                .saveOrGet(recordingUnitToSave, synchronousUnit, StratigraphicRelationshipService.SYNCHRONOUS);

        verify(stratigraphicRelationshipService, times(1))
                .saveOrGet(anteriorUnit, recordingUnitToSave, StratigraphicRelationshipService.ASYNCHRONOUS);

        verify(stratigraphicRelationshipService, times(1))
                .saveOrGet(recordingUnitToSave, posteriorUnit, StratigraphicRelationshipService.ASYNCHRONOUS);


    }

    @Test
    void save_Failure_MaxNbOfRecordingsReached() {

        Concept c = new Concept();
        c.setLabel("Unité strati");

        when(recordingUnitRepository.findMaxUsedIdentifierByAction(anyLong())).thenReturn(5);

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> recordingUnitService.save(recordingUnitToSave,c,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>())
        );

        assertEquals("Max recording unit code reached; Please ask administrator to increase the range", exception.getMessage());


    }

}