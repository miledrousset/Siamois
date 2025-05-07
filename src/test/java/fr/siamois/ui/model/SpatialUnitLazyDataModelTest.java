package fr.siamois.ui.model;

import fr.siamois.domain.models.Institution;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import fr.siamois.domain.services.SpatialUnitService;

import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpatialUnitLazyDataModelTest {

    @Mock
    private SpatialUnitService spatialUnitService;
    @Mock
    private SessionSettingsBean sessionSettingsBean;
    @Mock
    private LangBean langBean;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @InjectMocks
    private SpatialUnitLazyDataModel lazyModel;

    Page<SpatialUnit> p ;
    Pageable pageable;
    SpatialUnit spatialUnit1;
    SpatialUnit spatialUnit2;
    Institution institution;


    @BeforeEach
    void setUp() {
        spatialUnit1 = new SpatialUnit();
        spatialUnit2 = new SpatialUnit();
        institution = new Institution();
        institution.setId(1L);
        spatialUnit1.setId(1L);
        spatialUnit1.setName("Unit 1");
        spatialUnit2.setId(2L);
        p = new PageImpl<>(List.of(spatialUnit1, spatialUnit2));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void loadSpatialUnits_Success() {

        lazyModel = new SpatialUnitLazyDataModel(spatialUnitService,sessionSettingsBean,langBean);

        // Arrange
        when(spatialUnitService.findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(org.springframework.data.domain.Pageable.class)
        )).thenReturn(p);
        when(sessionSettingsBean.getSelectedInstitution()).thenReturn(institution);
        when(langBean.getLanguageCode()).thenReturn("en");

        // Act
        Page<SpatialUnit> actualResult = lazyModel.loadSpatialUnits("null", new Long[2], new Long[2], "null", pageable);

        // Assert
        // Assert
        assertEquals(spatialUnit1, actualResult.getContent().get(0));
        assertEquals(spatialUnit2, actualResult.getContent().get(1));
    }

    @Test
    void testLoad_withCategoryFilterAndAscSort() {

        lazyModel = Mockito.spy(new SpatialUnitLazyDataModel(spatialUnitService,sessionSettingsBean,langBean));



        // Arrange
        int first = 20;
        int pageSize = 10;

        // Filter setup
        Concept concept = new Concept();
        concept.setId(1L);
        ConceptLabel label = new ConceptLabel();
        label.setConcept(concept);
        List<ConceptLabel> categoryLabels = List.of(label);
        Person p = new Person();
        p.setId(1L);
        List<Person> persons = List.of(p);

        Map<String, FilterMeta> filters = new HashMap<>();
        FilterMeta catFilter = new FilterMeta();
        FilterMeta authorFilter = new FilterMeta();
        FilterMeta nameFilter = new FilterMeta();
        nameFilter.setFilterValue("name");
        FilterMeta globalFilter = new FilterMeta();
        globalFilter.setFilterValue("global");
        catFilter.setFilterValue(categoryLabels);
        authorFilter.setFilterValue(persons);
        filters.put("category", catFilter);
        filters.put("name", nameFilter);
        filters.put("author", authorFilter);
        filters.put("globalFilter", globalFilter);

        // Sort setup
        SortMeta sortMeta = new SortMeta();
        sortMeta.setOrder(SortOrder.ASCENDING);
        Map<String, SortMeta> sortBy = new HashMap<>();
        sortBy.put("category.label", sortMeta);

        // Mock data
        List<SpatialUnit> spatialUnits = new ArrayList<>();
        spatialUnits.add(spatialUnit1);
        for (int i = 0; i < 29; i++) {
            spatialUnits.add(spatialUnit2);
        }

        Page<SpatialUnit> page = new PageImpl<>(spatialUnits);

        doReturn(page).when(lazyModel).loadSpatialUnits(
                any(), any(), any(), any()

                ,any(Pageable.class)
        );

        // Act
        List<SpatialUnit> result = lazyModel.load(first, pageSize, sortBy, filters);

        // Assert
        assertEquals(30, result.size());
        assertEquals("Unit 1", result.get(0).getName());
        verify(lazyModel).loadSpatialUnits(
                eq("name"), eq(new Long[]{1L}),eq(new Long[]{1L}), eq("global"), pageableCaptor.capture()
        );

        Pageable capturedPageable = pageableCaptor.getValue();

        Sort.Order order = capturedPageable.getSort().getOrderFor("c_label");
        assertNotNull(order);
        assertEquals(2, capturedPageable.getPageNumber());
        assertEquals(10, capturedPageable.getPageSize());
        assertEquals(Sort.Direction.ASC, order.getDirection());

        // also test paginator value getters

        int firstIndexOnPage = lazyModel.getFirstIndexOnPage();
        int lastIndexOnPage = lazyModel.getLastIndexOnPage();

        assertEquals(21, firstIndexOnPage);
        assertEquals(30, lastIndexOnPage);


    }


}