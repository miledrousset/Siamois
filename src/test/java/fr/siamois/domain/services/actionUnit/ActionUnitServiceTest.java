package fr.siamois.domain.services.actionUnit;


import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionCode;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.actionunit.FailedActionUnitSaveException;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.infrastructure.repositories.actionunit.ActionCodeRepository;
import fr.siamois.infrastructure.repositories.actionunit.ActionUnitRepository;
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
import static org.mockito.ArgumentMatchers.any;
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


    @InjectMocks
    private ActionUnitService actionUnitService;


    SpatialUnit spatialUnit1;
    ActionUnit actionUnit1;
    ActionUnit actionUnit2;

    ActionUnit actionUnitWithCodes;
    ActionCode primaryActionCode;
    ActionCode secondaryActionCode1;
    ActionCode secondaryActionCode2;
    ActionCode failedCode;
    ActionCode secondaryActionCodeThatWillBeRemoved;
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

        actionUnitWithCodes = new ActionUnit();

        primaryActionCode = new ActionCode();
        primaryActionCode.setCode("primary");
        primaryActionCode.setType(c1);

        secondaryActionCode1 = new ActionCode();
        secondaryActionCode1.setCode("secondary1");
        secondaryActionCode1.setType(c2);

        secondaryActionCode2 = new ActionCode();
        secondaryActionCode2.setCode("secondary2");
        secondaryActionCode2.setType(c3);

        secondaryActionCodeThatWillBeRemoved = new ActionCode();
        secondaryActionCodeThatWillBeRemoved.setCode("willberemoved");

        actionUnitWithCodes.setPrimaryActionCode(primaryActionCode);
        actionUnitWithCodes.setSecondaryActionCodes(new HashSet<>(List.of(secondaryActionCode1, secondaryActionCode2, secondaryActionCodeThatWillBeRemoved)));

        actionUnitWithCodes.setIdentifier("Test");
        Institution institution = new Institution();
        institution.setIdentifier("MOM");
        actionUnitWithCodes.setCreatedByInstitution(institution);

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

        when(actionUnitRepository.findById(actionUnit1.getId())).thenReturn(Optional.empty());


        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> actionUnitService.findById(spatialUnit1.getId())
        );

        assertEquals("ActionUnit not found with ID: 1", exception.getMessage());

    }

    @Test
    void SaveWithActionCodes_Success() {

        lenient().when(conceptService.saveOrGetConcept(c1)).thenReturn(c1);
        lenient().when(conceptService.saveOrGetConcept(c2)).thenReturn(c2);
        lenient().when(conceptService.saveOrGetConcept(c3)).thenReturn(c3);

        lenient().when(actionCodeRepository.findById(primaryActionCode.getCode())).thenReturn(Optional.ofNullable(primaryActionCode));
        lenient().when(actionCodeRepository.findById(secondaryActionCode1.getCode())).thenReturn(Optional.ofNullable(secondaryActionCode1));
        lenient().when(actionCodeRepository.findById(secondaryActionCode2.getCode())).thenReturn(Optional.empty()); // It means this code is not in DB

        when(actionUnitRepository.save(actionUnitWithCodes)).thenReturn(actionUnitWithCodes);
        when(actionUnitRepository.findById(actionUnitWithCodes.getId())).thenReturn(Optional.ofNullable(actionUnitWithCodes));

        ActionUnit result = actionUnitService.save(actionUnitWithCodes, List.of(secondaryActionCode1, secondaryActionCode2),info);
        // assert
        assertNotNull(result);
        assertEquals(primaryActionCode, result.getPrimaryActionCode());
        assertEquals(new HashSet<>(List.of(secondaryActionCode1, secondaryActionCode2)), result.getSecondaryActionCodes());
        assertEquals("MOM-Test", result.getFullIdentifier());
    }

    @Test
    void Save_FailureBecauseIdentifierIsMissing() {

        lenient().when(conceptService.saveOrGetConcept(c1)).thenReturn(c1);
        lenient().when(conceptService.saveOrGetConcept(c2)).thenReturn(c2);
        lenient().when(conceptService.saveOrGetConcept(c3)).thenReturn(c3);

        lenient().when(actionCodeRepository.findById(primaryActionCode.getCode())).thenReturn(Optional.ofNullable(primaryActionCode));
        lenient().when(actionCodeRepository.findById(secondaryActionCode1.getCode())).thenReturn(Optional.ofNullable(secondaryActionCode1));
        lenient().when(actionCodeRepository.findById(secondaryActionCode2.getCode())).thenReturn(Optional.empty()); // It means this code is not in DB

        when(actionUnitRepository.findById(actionUnitWithCodes.getId())).thenReturn(Optional.ofNullable(actionUnitWithCodes));

        actionUnitWithCodes.setIdentifier(null); // remove identifier

        List<ActionCode> toSave = List.of(secondaryActionCode1, secondaryActionCode2);

        // Act & Assert
        Exception exception = assertThrows(
                FailedActionUnitSaveException.class,
                () -> actionUnitService.save(actionUnitWithCodes, toSave,info)
        );

        assertEquals("ActionUnit identifier must be set", exception.getMessage());
    }

    @Test
    void SaveActionCodes_FailedCodeExistsButTypeDoesNotMatch() {
        lenient().when(conceptService.saveOrGetConcept(c1)).thenReturn(c1);
        lenient().when(conceptService.saveOrGetConcept(c2)).thenReturn(c2);

        lenient().when(actionCodeRepository.findById(failedCode.getCode())).thenReturn(Optional.ofNullable(primaryActionCode));


        actionUnitWithCodes.setPrimaryActionCode(failedCode);

        List<ActionCode> toSave = List.of(secondaryActionCode1, secondaryActionCode2);

        // Act & Assert
        Exception exception = assertThrows(
                FailedActionUnitSaveException.class,
                () -> actionUnitService.save(actionUnitWithCodes, toSave,info)
        );

        assertEquals("Code exists but type does not match", exception.getMessage());
    }

    @Test
    void SaveActionCodes_Exception() {
        lenient().when(conceptService.saveOrGetConcept(c1)).thenReturn(c1);
        lenient().when(conceptService.saveOrGetConcept(c2)).thenReturn(c2);
        lenient().when(conceptService.saveOrGetConcept(c3)).thenReturn(c3);
        lenient().when(actionCodeRepository.findById(primaryActionCode.getCode())).thenReturn(Optional.ofNullable(primaryActionCode));
        lenient().when(actionCodeRepository.findById(secondaryActionCode1.getCode())).thenReturn(Optional.ofNullable(secondaryActionCode1));
        lenient().when(actionCodeRepository.findById(secondaryActionCode2.getCode())).thenReturn(Optional.empty());
        when(actionUnitRepository.save(actionUnitWithCodes)).thenThrow(new RuntimeException("Database error"));
        when(actionUnitRepository.findById(actionUnitWithCodes.getId())).thenReturn(Optional.ofNullable(actionUnitWithCodes));

        List<ActionCode> toSave = List.of(secondaryActionCode1, secondaryActionCode2);

        // Act & Assert
        Exception exception = assertThrows(
                FailedActionUnitSaveException.class,
                () -> actionUnitService.save(actionUnitWithCodes, toSave,info)
        );
        assertEquals("Database error", exception.getMessage());

    }

    @Test
    void findByArk() {
        ActionUnit actionUnit = new ActionUnit();
        actionUnit.setId(1L);

        Ark ark = new Ark();
        ark.setInternalId(1L);

        when(actionUnitRepository.findByArk(ark)).thenReturn(Optional.of(actionUnit));

        Optional<ActionUnit> result = actionUnitRepository.findByArk(ark);

        assertThat(result)
                .isPresent()
                .contains(actionUnit);
    }

    @Test
    void findWithoutArk() {
        Institution institution = new Institution();
        institution.setId(1L);

        ActionUnit actionUnitLocal = new ActionUnit();
        actionUnitLocal.setId(1L);
        ActionUnit actionUnitLocal2 = new ActionUnit();
        actionUnitLocal2.setId(2L);

        when(actionUnitRepository.findAllByArkIsNullAndCreatedByInstitution(institution)).thenReturn(List.of(actionUnitLocal, actionUnitLocal2));

        List<ActionUnit> result = actionUnitService.findWithoutArk(institution);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(actionUnitLocal));
        assertTrue(result.contains(actionUnitLocal2));
    }

    @Test
    void save() {
        ActionUnit actionUnit = new ActionUnit();
        actionUnit.setId(1L);

        when(actionUnitRepository.save(actionUnit)).thenReturn(actionUnit);

        ArkEntity result = actionUnitService.save(actionUnit);

        assertNotNull(result);
        assertEquals(actionUnit, result);
    }

    @Test
    void testFindByArk() {
        Ark ark = new Ark();
        ark.setInternalId(12L);
        ark.setQualifier("UBDQSD");

        ActionUnit actionUnit = new ActionUnit();
        actionUnit.setId(1L);
        actionUnit.setArk(ark);

        when(actionUnitRepository.findByArk(any(Ark.class))).thenReturn(Optional.of(actionUnit));

        Optional<ActionUnit> result = actionUnitService.findByArk(ark);

        assertTrue(result.isPresent());
    }
}