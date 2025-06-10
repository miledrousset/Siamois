package fr.siamois.domain.services;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.recordingunit.StratigraphicRelationship;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.form.CustomFormResponseService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.recordingunit.StratigraphicRelationshipService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.database.repositories.recordingunit.RecordingUnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


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
    @Mock
    private CustomFormResponseService customFormResponseService;

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

    Page<RecordingUnit> page ;
    Pageable pageable;

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
        recordingUnitToSave.setFormResponse(new CustomFormResponse());

        page = new PageImpl<>(List.of(recordingUnit1, recordingUnit2));
        pageable = PageRequest.of(0, 10);


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
        when(conceptService.saveOrGetConcept(c)).thenReturn(c);

        when(recordingUnitRepository.findMaxUsedIdentifierByAction(anyLong())).thenReturn(null);

        when(recordingUnitRepository.save(any(RecordingUnit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));



        RecordingUnit result = recordingUnitService.save(recordingUnitToSave,c,
                List.of(anteriorUnit),
                List.of(synchronousUnit),
                List.of(posteriorUnit)
        );

        // assert
        assertNotNull(result);
        assertNull(result.getFormResponse());
        assertEquals("MOM-2025-5", result.getFullIdentifier());



    }

    @Test
    void save_saveFormIfSet() {

        CustomForm form = new CustomForm();
        recordingUnitToSave.setFormResponse(new CustomFormResponse());
        recordingUnitToSave.getFormResponse().setForm(form);

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
        when(conceptService.saveOrGetConcept(c)).thenReturn(c);

        when(recordingUnitRepository.findMaxUsedIdentifierByAction(anyLong())).thenReturn(null);

        when(recordingUnitRepository.save(any(RecordingUnit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RecordingUnit res = recordingUnitService.save(recordingUnitToSave, c, List.of(anteriorUnit),
                List.of(synchronousUnit),
                List.of(posteriorUnit));
        assertNotNull(res);
        assertNotNull(res.getFormResponse());



    }

    @Test
    void save_Failure_MaxNbOfRecordingsReached() {

        Concept c = new Concept();

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

    @Test
    void testFindAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining_Success() {

        when(recordingUnitRepository.findAllByInstitutionAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<RecordingUnit> actualResult = recordingUnitService.findAllByInstitutionAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                1L, "null", new Long[2], "null", "fr", pageable
        );

        // Assert
        assertEquals(recordingUnit1, actualResult.getContent().get(0));
        assertEquals(recordingUnit2, actualResult.getContent().get(1));
    }

}