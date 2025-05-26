package fr.siamois.ui.lazydatamodel;

import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpatialUnitChildrenLazyDataModelTest {

    @Mock
    private SpatialUnitService spatialUnitService;
    @Mock
    private SessionSettingsBean sessionSettingsBean;
    @Mock
    private LangBean langBean;

    @InjectMocks
    private SpatialUnitChildrenLazyDataModel lazyModel;

    Page<SpatialUnit> p ;
    Pageable pageable;
    SpatialUnit spatialUnit1;
    SpatialUnit spatialUnit2;
    SpatialUnit spatialUnit3;
    Institution institution;

    @BeforeEach
    void setUp() {
        spatialUnit1 = new SpatialUnit();
        spatialUnit2 = new SpatialUnit();
        spatialUnit3 = new SpatialUnit();
        institution = new Institution();
        institution.setId(1L);
        spatialUnit1.setId(1L);
        spatialUnit2.setId(2L);
        p = new PageImpl<>(List.of(spatialUnit1, spatialUnit2));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void loadSpatialUnits() {

        lazyModel = new SpatialUnitChildrenLazyDataModel(spatialUnitService,langBean, spatialUnit3);

        // Arrange
        when(spatialUnitService.findAllByParentAndByNameContainingAndByCategoriesAndByGlobalContaining(
                ArgumentMatchers.any(SpatialUnit.class),
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(Long[].class),
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(org.springframework.data.domain.Pageable.class)
        )).thenReturn(p);
        when(langBean.getLanguageCode()).thenReturn("en");

        // Act
        Page<SpatialUnit> actualResult = lazyModel.loadSpatialUnits("null", new Long[2], new Long[2],"null", pageable);

        // Assert
        // Assert
        assertEquals(spatialUnit1, actualResult.getContent().get(0));
        assertEquals(spatialUnit2, actualResult.getContent().get(1));
    }
}