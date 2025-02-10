package fr.siamois.services.actionUnit;


import fr.siamois.infrastructure.repositories.actionunit.ActionCodeRepository;
import fr.siamois.infrastructure.repositories.actionunit.ActionUnitRepository;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.UserInfo;
import fr.siamois.models.actionunit.ActionCode;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.FailedActionUnitSaveException;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.vocabulary.Concept;

import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.services.vocabulary.ConceptService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionUnitServiceTest {

    @Mock
    private ActionUnitRepository actionUnitRepository;
    @Mock
    private ConceptService conceptService;
    @Mock
    private ActionCodeRepository actionCodeRepository;
    @Mock
    private ArkServerRepository arkServerRepository;


    @InjectMocks
    private ActionUnitService actionUnitService;


    SpatialUnit spatialUnit1;
    ActionUnit actionUnit1;
    ActionUnit actionUnit2;

    ActionUnit actionUnitWithCodesBefore;
    ActionUnit actionUnitWithCodesAfter;
    ActionCode primaryActionCode;
    ActionCode primaryActionCodeBefore;
    ActionCode secondaryActionCode1;
    ActionCode secondaryActionCode2;
    ActionCode failedCode;
    Concept c1, c2, c3;

    UserInfo info;

    @BeforeEach
    void setUp() {

        Person p =new Person();
        Institution i = new Institution();
        info = new UserInfo(i,p,"fr");
        spatialUnit1 = new SpatialUnit();
        actionUnit1 = new ActionUnit();
        actionUnit2 = new ActionUnit();
        spatialUnit1.setId(1L);
        actionUnit1.setId(1L);
        actionUnit2.setId(2L);
        c1 = new Concept();
        c2 = new Concept();
        c3 = new Concept();
        // For action codes test
        c1.setExternalId("1");
        c1.setLabel("Code OA");
        c2.setExternalId("2");
        c2.setLabel("Code OB");
        c3.setExternalId("3");
        c3.setLabel("Code libre");

        actionUnitWithCodesAfter = new ActionUnit();
        actionUnitWithCodesBefore = new ActionUnit();
        primaryActionCode = new ActionCode();
        primaryActionCode.setCode("primary");
        primaryActionCode.setType(c1);
        primaryActionCode = new ActionCode();
        primaryActionCodeBefore = new ActionCode();
        primaryActionCodeBefore.setCode("primaryBefore");
        primaryActionCodeBefore.setType(c2);
        secondaryActionCode1 = new ActionCode();
        secondaryActionCode1.setCode("secondary1");
        secondaryActionCode1.setType(c2);
        secondaryActionCode2 = new ActionCode();
        secondaryActionCode2.setCode("secondary2");
        secondaryActionCode2.setType(c3);
        actionUnitWithCodesBefore.setPrimaryActionCode(primaryActionCodeBefore);
        actionUnitWithCodesAfter.setPrimaryActionCode(primaryActionCode);
        actionUnitWithCodesAfter.setSecondaryActionCodes(new HashSet<>(List.of(secondaryActionCode1, secondaryActionCode2)));

        failedCode = new ActionCode();
        failedCode.setType(c2);
        failedCode.setCode("primary");



    }

    @Test
    void findAllBySpatialUnitId() {

        when(actionUnitRepository.findAllBySpatialUnitId(spatialUnit1.getId())).thenReturn(List.of(actionUnit1, actionUnit2));

        // Act
        List<ActionUnit> actualResult = actionUnitService.findAllBySpatialUnitId(spatialUnit1);

        // Assert
        assertEquals(List.of(actionUnit1, actionUnit2), actualResult);
    }

    @Test
    void findAllBySpatialUnitId_Exception() {

        // Arrange
        when(actionUnitRepository.findAllBySpatialUnitId(spatialUnit1.getId())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> actionUnitService.findAllBySpatialUnitId(spatialUnit1)
        );

        assertEquals("Database error", exception.getMessage());

    }

    @Test
    void findById_success() {

        when(actionUnitRepository.findById(actionUnit1.getId())).thenReturn(Optional.ofNullable(actionUnit1));

        // act
        ActionUnit actualResult = actionUnitService.findById(spatialUnit1.getId());

        // assert
        assertEquals(actionUnit1, actualResult);
    }

    @Test
    void findById_Exception() {

        when(actionUnitRepository.findById(actionUnit1.getId())).thenReturn(Optional.ofNullable(null));


        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> actionUnitService.findById(spatialUnit1.getId())
        );

        assertEquals("ActionUnit not found with ID: 1", exception.getMessage());

    }

    @Test
    void SaveWithActionCodes_Success() {
        when(arkServerRepository.findLocalServer()).thenReturn(Optional.of(new ArkServer()));
        lenient().when(conceptService.saveOrGetConcept(c1)).thenReturn(c1);
        lenient().when(conceptService.saveOrGetConcept(c2)).thenReturn(c2);
        lenient().when(conceptService.saveOrGetConcept(c3)).thenReturn(c3);
        lenient().when(actionCodeRepository.findById(primaryActionCode.getCode())).thenReturn(Optional.ofNullable(primaryActionCodeBefore));
        lenient().when(actionCodeRepository.findById(secondaryActionCode1.getCode())).thenReturn(Optional.ofNullable(secondaryActionCode1));
        lenient().when(actionCodeRepository.findById(secondaryActionCode2.getCode())).thenReturn(Optional.empty());
        when(actionUnitRepository.save(actionUnitWithCodesBefore)).thenReturn(actionUnitWithCodesAfter);
        when(actionUnitRepository.findById(actionUnitWithCodesBefore.getId())).thenReturn(Optional.ofNullable(actionUnitWithCodesBefore));

        ActionUnit result = actionUnitService.save(actionUnitWithCodesBefore, List.of(secondaryActionCode1, secondaryActionCode2),info);
        // assert
        assertEquals(actionUnitWithCodesAfter, result);
    }

    @Test
    void SaveActionCodes_FailedCodeExistsButTypeDoesNotMatch() {
        lenient().when(conceptService.saveOrGetConcept(c1)).thenReturn(c1);
        lenient().when(conceptService.saveOrGetConcept(c2)).thenReturn(c2);

        lenient().when(actionCodeRepository.findById(failedCode.getCode())).thenReturn(Optional.ofNullable(primaryActionCode));


        actionUnitWithCodesBefore.setPrimaryActionCode(failedCode);

        // Act & Assert
        Exception exception = assertThrows(
                FailedActionUnitSaveException.class,
                () -> actionUnitService.save(actionUnitWithCodesBefore, List.of(secondaryActionCode1, secondaryActionCode2),info)
        );

        assertEquals("Code exists but type does not match", exception.getMessage());
    }

    @Test
    void SaveActionCodes_Exception() {
        when(arkServerRepository.findLocalServer()).thenReturn(Optional.of(new ArkServer()));
        lenient().when(conceptService.saveOrGetConcept(c1)).thenReturn(c1);
        lenient().when(conceptService.saveOrGetConcept(c2)).thenReturn(c2);
        lenient().when(conceptService.saveOrGetConcept(c3)).thenReturn(c3);
        lenient().when(actionCodeRepository.findById(primaryActionCode.getCode())).thenReturn(Optional.ofNullable(primaryActionCodeBefore));
        lenient().when(actionCodeRepository.findById(secondaryActionCode1.getCode())).thenReturn(Optional.ofNullable(secondaryActionCode1));
        lenient().when(actionCodeRepository.findById(secondaryActionCode2.getCode())).thenReturn(Optional.empty());
        when(actionUnitRepository.save(actionUnitWithCodesBefore)).thenThrow(new RuntimeException("Database error"));
        when(actionUnitRepository.findById(actionUnitWithCodesBefore.getId())).thenReturn(Optional.ofNullable(actionUnitWithCodesBefore));

        // Act & Assert
        Exception exception = assertThrows(
                FailedActionUnitSaveException.class,
                () -> actionUnitService.save(actionUnitWithCodesBefore, List.of(secondaryActionCode1, secondaryActionCode2),info)
        );
        assertEquals("Database error", exception.getMessage());

    }
}