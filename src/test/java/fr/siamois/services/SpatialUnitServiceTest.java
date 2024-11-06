package fr.siamois.services;

import fr.siamois.exceptions.SpatialUnitNotFoundException;
import fr.siamois.models.SpatialUnit;
import fr.siamois.repositories.SpatialUnitRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpatialUnitServiceTest {

    @Mock
    private SpatialUnitRepository spatialUnitRepository;

    @InjectMocks
    private SpatialUnitService spatialUnitService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllWithoutParents() {
    }

    @Test
    void findAllChildOfSpatialUnit() {
    }

    @Test
    public void testFindById_Success() {

        // Arrange
        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setId(1);
        when(spatialUnitRepository.findById(1)).thenReturn(Optional.of(spatialUnit));

        // Act
        SpatialUnit actualResult = spatialUnitService.findById(1);

        // Assert
        assertEquals(spatialUnit, actualResult);
    }

    @Test
    public void testFindById_SpatialUnitNotFoundException() {
        // Arrange
        int id = 1;
        when(spatialUnitRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        SpatialUnitNotFoundException exception = assertThrows(
                SpatialUnitNotFoundException.class,
                () -> spatialUnitService.findById(id)
        );

        assertEquals("SpatialUnit not found with ID: " + id, exception.getMessage());
    }

    @Test
    public void testFindById_RuntimeException() {
        // Arrange
        int id = 1;
        when(spatialUnitRepository.findById(id)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> spatialUnitService.findById(id)
        );

        assertEquals("Database error", exception.getCause().getMessage());

    }
}