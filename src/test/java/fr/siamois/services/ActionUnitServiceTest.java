package fr.siamois.services;


import fr.siamois.infrastructure.repositories.actionunit.ActionCodeRepository;
import fr.siamois.infrastructure.repositories.actionunit.ActionUnitRepository;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.UserInfo;
import fr.siamois.models.actionunit.ActionCode;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.services.vocabulary.ConceptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionUnitServiceTest {

    @Mock private ActionUnitRepository actionUnitRepository;
    @Mock private ArkServerRepository arkServerRepository;
    @Mock private ConceptService conceptService;
    @Mock private ActionCodeRepository actionCodeRepository;

    @InjectMocks
    private ActionUnitService actionUnitService;

    SpatialUnit spatialUnit1 ;
    ActionUnit actionUnit1 ;
    ActionUnit actionUnit2 ;

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
        spatialUnit1 = new SpatialUnit();
        actionUnit1 = new ActionUnit();
        actionUnit2 = new ActionUnit();
        spatialUnit1.setId(1L);
        actionUnit1.setId(1L);
        actionUnit2.setId(2L);

        Person p =new Person();
        Institution i = new Institution();
        info = new UserInfo(i,p,"fr");
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
        assertEquals(List.of(actionUnit1,actionUnit2), actualResult);
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

        when(actionUnitRepository.findById(actionUnit1.getId())).thenReturn(Optional.empty());


        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> actionUnitService.findById(spatialUnit1.getId())
        );

        assertEquals("ActionUnit not found with ID: 1", exception.getMessage());
    }

    @Test
    void save_withUserInfo_success() {
        UserInfo userInfo = new UserInfo(new Institution(), new Person(), "fr");

        ActionUnit actionUnit = new ActionUnit();
        actionUnit.setIdentifier("Test");
        Institution institution = new Institution();
        institution.setIdentifier("MOM");
        actionUnit.setCreatedByInstitution(institution);
        Concept typeConcept = new Concept();
        ArkServer localServer = new ArkServer();
        localServer.setId(1L);
        Ark ark = new Ark();
        ark.setArkServer(localServer);


        when(arkServerRepository.findLocalServer()).thenReturn(Optional.of(localServer));
        when(conceptService.saveOrGetConcept(typeConcept)).thenReturn(typeConcept);
        when(actionUnitRepository.save(actionUnit)).thenReturn(actionUnit);

        ActionUnit result = actionUnitService.save(userInfo, actionUnit, typeConcept);

        assertNotNull(result);
        assertEquals("MOM-Test", result.getFullIdentifier());
        assertEquals(actionUnit, result);
        assertEquals(typeConcept, result.getType());
        assertEquals(userInfo.getUser(), result.getAuthor());
        assertEquals(userInfo.getInstitution(), result.getCreatedByInstitution());
        assertNotNull(result.getArk());
        assertThat(result.getArk().getArkId()).startsWith("666666/");
    }

    @Test
    void save_withUserInfo_failure() {
        UserInfo userInfo = new UserInfo(new Institution(), new Person(), "fr");

        ActionUnit actionUnit = new ActionUnit();
        Concept typeConcept = new Concept();

        when(arkServerRepository.findLocalServer()).thenThrow(new RuntimeException("No local server found"));

        Exception exception = assertThrows(
                FailedRecordingUnitSaveException.class,
                () -> actionUnitService.save(userInfo, actionUnit, typeConcept)
        );

        assertEquals("No local server found", exception.getMessage());
    }

    @Test
    void findAllActionCodeByCodeIsContainingIgnoreCase_Success() {
        // Arrange
        String query = "test";
        ActionCode actionCode1 = new ActionCode();
        actionCode1.setCode("testCode1");
        ActionCode actionCode2 = new ActionCode();
        actionCode2.setCode("anotherTestCode");
        when(actionCodeRepository.findAllByCodeIsContainingIgnoreCase(query)).thenReturn(List.of(actionCode1, actionCode2));

        // Act
        List<ActionCode> actualResult = actionUnitService.findAllActionCodeByCodeIsContainingIgnoreCase(query);

        // Assert
        assertNotNull(actualResult);
        assertEquals(2, actualResult.size());
        assertThat(actualResult).extracting(ActionCode::getCode).containsExactlyInAnyOrder("testCode1", "anotherTestCode");
    }

    @Test
    void findAllActionCodeByCodeIsContainingIgnoreCase_Exception() {
        // Arrange
        String query = "test";
        when(actionCodeRepository.findAllByCodeIsContainingIgnoreCase(query)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                RuntimeException.class,
                () -> actionUnitService.findAllActionCodeByCodeIsContainingIgnoreCase(query)
        );

        assertEquals("Database error", exception.getMessage());
    }


}