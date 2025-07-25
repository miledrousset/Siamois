package fr.siamois.domain.services;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.spatialunit.SpatialUnitAlreadyExistsException;
import fr.siamois.domain.models.exceptions.spatialunit.SpatialUnitNotFoundException;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.spatialunit.SpatialUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.infrastructure.database.repositories.SpatialUnitRepository;
import lombok.extern.slf4j.Slf4j;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class SpatialUnitServiceTest {

    @Mock
    private SpatialUnitRepository spatialUnitRepository;


    @Mock
    private PersonService personService;

    @Mock
    private ConceptService conceptService;

    @Mock
    private InstitutionService institutionService;

    @InjectMocks
    private SpatialUnitService spatialUnitService;

    SpatialUnit spatialUnit1;

    SpatialUnit spatialUnit2;

    Page<SpatialUnit> p ;
    Pageable pageable;


    @BeforeEach
    void setUp() {
        spatialUnit1 = new SpatialUnit();
        spatialUnit2 = new SpatialUnit();
        spatialUnit1.setId(1L);
        spatialUnit2.setId(2L);
        p = new PageImpl<>(List.of(spatialUnit1, spatialUnit2));
        pageable = PageRequest.of(0, 10);


        lenient().when(spatialUnitRepository.findAllByParentAndByNameContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(Pageable.class)
        )).thenReturn(p);
        lenient().when(spatialUnitRepository.findAllByChildAndByNameContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(Pageable.class)
        )).thenReturn(p);

    }

    @Test
    void testFindAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining_Success() {

        when(spatialUnitRepository.findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(Pageable.class)
        )).thenReturn(p);

        // Act
        Page<SpatialUnit> actualResult = spatialUnitService.findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
                1L, "null", new Long[2], new Long[2],"null", "fr", pageable
        );

        // Assert
        assertEquals(spatialUnit1, actualResult.getContent().get(0));
        assertEquals(spatialUnit2, actualResult.getContent().get(1));
    }

    @Test
    void testFindAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining_Exception() {

        // Arrange
        when(spatialUnitRepository.findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(Pageable.class)
        )).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> spatialUnitService.findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
                        1L, "null", new Long[2], new Long[2], "null", "fr", pageable
                )
        );

        assertEquals("Database error", exception.getMessage());

    }

    @Test
    void findAllChildOfSpatialUnit_Success() {

        when(spatialUnitRepository.findAllByParentAndByNameContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(Pageable.class)
        )).thenReturn(p);

        // Act
        Page<SpatialUnit> actualResult = spatialUnitService.findAllByParentAndByNameContainingAndByCategoriesAndByGlobalContaining(
                spatialUnit1, "null", new Long[2], new Long[2],"null", "fr", pageable);


        // Assert
        // Assert
        assertEquals(spatialUnit1, actualResult.getContent().get(0));
        assertEquals(spatialUnit2, actualResult.getContent().get(1));

    }

    @Test
    void findAllChildOfSpatialUnit_Exception() {

        // Arrange
        when(spatialUnitRepository.findAllByParentAndByNameContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(Pageable.class)
        )).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> spatialUnitService.findAllByParentAndByNameContainingAndByCategoriesAndByGlobalContaining(
                        spatialUnit1, "null", new Long[2],new Long[2], "null", "fr", pageable)
        );

        assertEquals("Database error", exception.getMessage());

    }

    @Test
    void testFindById_Success() {

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
    void testFindById_SpatialUnitNotFoundException() {
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
    void testFindById_Exception() {
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

    @Test
    void findAllParentsOfSpatialUnit_Success() {

        // Arrange
        when(spatialUnitRepository.findAllByChildAndByNameContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(Pageable.class)
        )).thenReturn(p);

        // Act
        Page<SpatialUnit> actualResult = spatialUnitService.findAllByChildAndByNameContainingAndByCategoriesAndByGlobalContaining(
                spatialUnit1, "null", new Long[2], new Long[2], "null", "fr", pageable);

        // Assert
        assertEquals(spatialUnit1, actualResult.getContent().get(0));
        assertEquals(spatialUnit2, actualResult.getContent().get(1));

    }

    @Test
    void findAllParentsOfSpatialUnit_Exception() {
        // Arrange
        when(spatialUnitRepository.findAllByChildAndByNameContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(Pageable.class)
        )).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> spatialUnitService.findAllByChildAndByNameContainingAndByCategoriesAndByGlobalContaining(
                        spatialUnit1, "null", new Long[2], new Long[2], "null", "fr", pageable)
        );

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void restore_Success() {
        // Arrange
        SpatialUnitHist history = new SpatialUnitHist();
        SpatialUnit spatialUnit = new SpatialUnit();
        when(spatialUnitRepository.save(any(SpatialUnit.class))).thenReturn(spatialUnit);

        // Act
        spatialUnitService.restore(history);

        // Assert
        verify(spatialUnitRepository).save(any(SpatialUnit.class));
    }


    @Test
    void findAllOfInstitution_Success() {
        // Arrange
        Institution institution = new Institution();
        institution.setId(1L);
        when(spatialUnitRepository.findAllOfInstitution(institution.getId())).thenReturn(List.of(spatialUnit1, spatialUnit2));

        // Act
        List<SpatialUnit> actualResult = spatialUnitService.findAllOfInstitution(institution);

        // Assert
        assertEquals(List.of(spatialUnit1, spatialUnit2), actualResult);
    }

    @Test
    void findAllOfInstitution_Exception() {
        // Arrange
        Institution institution = new Institution();
        institution.setId(1L);
        when(spatialUnitRepository.findAllOfInstitution(institution.getId())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class,
                () -> spatialUnitService.findAllOfInstitution(institution)
        );

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void save_Success() throws SpatialUnitAlreadyExistsException {
        // Arrange
        Person person = new Person();
        person.setId(1L);
        Institution i = new Institution();
        i.setId(1L);
        UserInfo userInfo = new UserInfo(i ,person, "fr");
        String name = "SpatialUnitName";
        Concept type = new Concept();
        List<SpatialUnit> parents = List.of(spatialUnit1);
        SpatialUnit unit = new SpatialUnit();
        unit.setName(name);
        unit.setCategory(type);
        unit.setParents(new HashSet<>(parents));




        when(institutionService.createOrGetSettingsOf(userInfo.getInstitution())).thenReturn(new InstitutionSettings());
        when(spatialUnitRepository.findByNameAndInstitution(name, userInfo.getInstitution().getId())).thenReturn(Optional.empty());
        when(conceptService.saveOrGetConcept(type)).thenReturn(type);
        when(spatialUnitRepository.save(any(SpatialUnit.class))).thenReturn(spatialUnit1);
        when(institutionService.findById(anyLong())).thenReturn(i);
        when(personService.findById(anyLong())).thenReturn(person);

        // Act
        SpatialUnit result = spatialUnitService.save(userInfo, unit);

        // Assert
        assertNotNull(result);
        assertEquals(spatialUnit1, result);
        verify(spatialUnitRepository).addParentToSpatialUnit(spatialUnit1.getId(), spatialUnit1.getId());
    }

    @Test
    void save_SpatialUnitAlreadyExistsException() {
        // Arrange
        UserInfo userInfo = new UserInfo(new Institution(), new Person(), "fr");

        String name = "SpatialUnitName";
        Concept type = new Concept();
        List<SpatialUnit> parents = List.of(spatialUnit1);
        SpatialUnit unit = new SpatialUnit();
        unit.setName(name);
        unit.setCategory(type);
        unit.setParents(new HashSet<>(parents));

        when(spatialUnitRepository.findByNameAndInstitution(name, userInfo.getInstitution().getId())).thenReturn(Optional.of(spatialUnit1));

        // Act & Assert
        SpatialUnitAlreadyExistsException exception = assertThrows(
                SpatialUnitAlreadyExistsException.class,
                () -> spatialUnitService.save(userInfo, unit)
        );

        assertEquals("Spatial Unit with name SpatialUnitName already exist in institution null", exception.getMessage());
    }

    @Test
    void findByArk() {
        // Arrange
        Ark ark = new Ark();
        SpatialUnit spatialUnit = new SpatialUnit();
        when(spatialUnitRepository.findByArk(ark)).thenReturn(Optional.of(spatialUnit));

        // Act
        Optional<SpatialUnit> result = spatialUnitService.findByArk(ark);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(spatialUnit, result.get());
        verify(spatialUnitRepository, times(1)).findByArk(ark);
    }

    @Test
    void findWithoutArk() {
        // Arrange
        Institution institution = new Institution();
        institution.setId(1L);
        SpatialUnit spatialUnit = new SpatialUnit();
        when(spatialUnitRepository.findAllByArkIsNullAndCreatedByInstitution(institution))
                .thenReturn(List.of(spatialUnit));

        // Act
        List<? extends ArkEntity> result = spatialUnitService.findWithoutArk(institution);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(spatialUnit, result.get(0));
        verify(spatialUnitRepository, times(1)).findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Test
    void save() {
        // Arrange
        SpatialUnit spatialUnit = new SpatialUnit();
        when(spatialUnitRepository.save(spatialUnit)).thenReturn(spatialUnit);

        // Act
        ArkEntity result = spatialUnitService.save(spatialUnit);

        // Assert
        assertNotNull(result);
        assertEquals(spatialUnit, result);
        verify(spatialUnitRepository, times(1)).save(spatialUnit);
    }

    @Test
    void countByInstitution_success() {
        when(spatialUnitRepository.countByCreatedByInstitution(any(Institution.class))).thenReturn(3L);
        assertEquals(3, spatialUnitService.countByInstitution(new Institution()));
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        spatialUnit1 = new SpatialUnit();
        spatialUnit2 = new SpatialUnit();
        when(spatialUnitRepository.findAll()).thenReturn(List.of(spatialUnit1, spatialUnit2));

        // Act
        List<SpatialUnit> actualResult = spatialUnitService.findAll();

        // Assert
        assertNotNull(actualResult);
        assertEquals(2, actualResult.size());
        assertTrue(actualResult.contains(spatialUnit1));
        assertTrue(actualResult.contains(spatialUnit2));
        verify(spatialUnitRepository, times(1)).findAll();
    }

    @Test
    void test_countChildrenByParent() {
        SpatialUnit su = new SpatialUnit();
        su.setId(1L);

        when(spatialUnitRepository.countChildrenByParentId(1L)).thenReturn(1L);

        long result = spatialUnitService.countChildrenByParent(su);

        assertEquals(1L, result);
    }

    @Test
    void test_countParentByChild() {
        SpatialUnit su = new SpatialUnit();
        su.setId(1L);

        when(spatialUnitRepository.countParentsByChildId(1L)).thenReturn(1L);

        long result = spatialUnitService.countParentsByChild(su);

        assertEquals(1L, result);
    }

    @Test
    void test_findRootsOf() {
        SpatialUnit su1 = new SpatialUnit();
        su1.setId(1L);

        SpatialUnit su2 = new SpatialUnit();
        su2.setId(2L);

        SpatialUnit su3 = new SpatialUnit();
        su3.setId(3L);

        Institution institution = new Institution();
        institution.setId(1L);

        when(spatialUnitRepository.findAllOfInstitution(institution.getId())).thenReturn(List.of(su1,su2,su3));
        when(spatialUnitRepository.countParentsByChildId(su1.getId())).thenReturn(0L);
        when(spatialUnitRepository.countParentsByChildId(su2.getId())).thenReturn(1L);
        when(spatialUnitRepository.countParentsByChildId(su3.getId())).thenReturn(1L);

        List<SpatialUnit> roots = spatialUnitService.findRootsOf(institution);

        assertThat(roots)
                .hasSize(1)
                .containsExactlyInAnyOrder(su1);
    }

    @Test
    void test_findDirectChildrensOf() {
        SpatialUnit su1 = new SpatialUnit();
        su1.setId(1L);

        SpatialUnit su2 = new SpatialUnit();
        su2.setId(2L);

        SpatialUnit su3 = new SpatialUnit();
        su3.setId(3L);

        when(spatialUnitRepository.findChildrensOf(su1.getId())).thenReturn(Set.of(su2,su3));

        List<SpatialUnit> result = spatialUnitService.findDirectChildrensOf(su1);

        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(su2,su3);
    }

    @Test
    void test_neighborMapOfAllSpatialUnit() {
        SpatialUnit su1 = new SpatialUnit();
        su1.setId(1L);

        SpatialUnit su2 = new SpatialUnit();
        su2.setId(2L);

        SpatialUnit su3 = new SpatialUnit();
        su3.setId(3L);

        SpatialUnit su4 = new SpatialUnit();
        su4.setId(4L);

        Institution institution = new Institution();
        institution.setId(2L);

        when(spatialUnitRepository.findAllOfInstitution(institution.getId())).thenReturn(List.of(su1, su2, su3, su4));

        when(spatialUnitRepository.countParentsByChildId(su1.getId())).thenReturn(0L);
        when(spatialUnitRepository.countParentsByChildId(su2.getId())).thenReturn(1L);
        when(spatialUnitRepository.countParentsByChildId(su3.getId())).thenReturn(1L);
        when(spatialUnitRepository.countParentsByChildId(su4.getId())).thenReturn(1L);

        when(spatialUnitRepository.findChildrensOf(su1.getId())).thenReturn(Set.of(su2, su3));
        when(spatialUnitRepository.findChildrensOf(su3.getId())).thenReturn(Set.of(su4));
        when(spatialUnitRepository.findChildrensOf(su2.getId())).thenReturn(Set.of());
        when(spatialUnitRepository.findChildrensOf(su4.getId())).thenReturn(Set.of());

        // Appel de la méthode à tester
        Map<SpatialUnit, List<SpatialUnit>> neighborMap = spatialUnitService.neighborMapOfAllSpatialUnit(institution);

        // Vérifications
        assertNotNull(neighborMap);
        assertEquals(4, neighborMap.size());
        assertTrue(neighborMap.get(su1).containsAll(List.of(su2, su3)));
        assertTrue(neighborMap.get(su3).contains(su4));
        assertTrue(neighborMap.get(su2).isEmpty());
        assertTrue(neighborMap.get(su4).isEmpty());

        assertThat(neighborMap)
                .isNotNull()
                .hasSize(4)
                .containsKeys(su1, su2, su3, su4);

        assertThat(neighborMap.get(su1)).containsExactlyInAnyOrder(su2, su3);
        assertThat(neighborMap.get(su2)).isEmpty();
        assertThat(neighborMap.get(su3)).containsExactlyInAnyOrder(su4);
        assertThat(neighborMap.get(su4)).isEmpty();
    }

}