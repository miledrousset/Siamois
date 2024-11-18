package fr.siamois.services;

import fr.siamois.models.SpatialUnit;
import fr.siamois.models.exceptions.SpatialUnitNotFoundException;
import fr.siamois.repositories.SpatialUnitRepository;
import lombok.extern.slf4j.Slf4j;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class SpatialUnitServiceTest {

    @Mock
    private SpatialUnitRepository spatialUnitRepository;

    @InjectMocks
    private SpatialUnitService spatialUnitService;

    SpatialUnit spatialUnit1 ;

    SpatialUnit spatialUnit2 ;


    @BeforeEach
    void setUp() {
        spatialUnit1 = new SpatialUnit();
        spatialUnit2 = new SpatialUnit();
        spatialUnit1.setId(1L);
        spatialUnit2.setId(2L);
        lenient().when(spatialUnitRepository.findAllWithoutParents()).thenReturn(List.of(spatialUnit1, spatialUnit2));
        lenient().when(spatialUnitRepository.findAllChildOfSpatialUnit(spatialUnit1.getId())).thenReturn(List.of(spatialUnit2));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllWithoutParents_Success() {

        // Act
        List<SpatialUnit> actualResult = spatialUnitService.findAllWithoutParents();

        // Assert
        assertEquals(List.of(spatialUnit1, spatialUnit2), actualResult);
    }

    @Test
    void testFindAllWithoutParents_Exception() {

        // Arrange
        when(spatialUnitRepository.findAllWithoutParents()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> spatialUnitService.findAllWithoutParents()
        );

        assertEquals("Database error", exception.getMessage());

    }

    @Test
    void findAllChildOfSpatialUnit_Success() {

        // Act
        List<SpatialUnit> actualResult = spatialUnitService.findAllChildOfSpatialUnit(spatialUnit1);

        // Assert
        assertEquals(List.of(spatialUnit2), actualResult);

    }

    @Test
    void findAllChildOfSpatialUnit_Exception() {

        // Arrange
        when(spatialUnitRepository.findAllChildOfSpatialUnit(spatialUnit1.getId())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> spatialUnitService.findAllChildOfSpatialUnit(spatialUnit1)
        );

        assertEquals("Database error", exception.getMessage());

    }

    @Test
    public void testFindById_Success() {

        // Arrange
        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setId(1L);
        when(spatialUnitRepository.findById(1L)).thenReturn(Optional.of(spatialUnit));

        // Act
        SpatialUnit actualResult = spatialUnitService.findById(1);

        // Assert
        assertEquals(spatialUnit, actualResult);
    }

    @Test
    public void testFindById_SpatialUnitNotFoundException() {
        // Arrange
        long id = 1;
        when(spatialUnitRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        SpatialUnitNotFoundException exception = assertThrows(
                SpatialUnitNotFoundException.class,
                () -> spatialUnitService.findById(id)
        );

        assertEquals("SpatialUnit not found with ID: " + id, exception.getMessage());
    }

    @Test
    public void testFindById_Exception() {
        // Arrange
        long id = 1;
        when(spatialUnitRepository.findById(id)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> spatialUnitService.findById(id)
        );

        assertEquals("Database error", exception.getMessage());

    }
}