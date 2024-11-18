package fr.siamois.services;

import fr.siamois.models.ActionUnit;
import fr.siamois.models.RecordingUnit;
import fr.siamois.models.SpatialUnit;
import fr.siamois.repositories.ActionUnitRepository;
import fr.siamois.repositories.RecordingUnitRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionUnitServiceTest {

    @Mock
    private ActionUnitRepository actionUnitRepository;

    @InjectMocks
    private ActionUnitService actionUnitService;

    SpatialUnit spatialUnit1 ;
    ActionUnit actionUnit1 ;
    ActionUnit actionUnit2 ;

    @BeforeEach
    void setUp() {
        spatialUnit1 = new SpatialUnit();
        actionUnit1 = new ActionUnit();
        actionUnit2 = new ActionUnit();
        spatialUnit1.setId(1);
        actionUnit1.setId(1);
        actionUnit2.setId(2);
        when(actionUnitRepository.findAllBySpatialUnitId(actionUnit1.getId())).thenReturn(List.of(actionUnit1, actionUnit2));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllBySpatialUnitId() {
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
}