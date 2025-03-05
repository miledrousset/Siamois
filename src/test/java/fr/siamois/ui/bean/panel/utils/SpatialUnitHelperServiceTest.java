package fr.siamois.ui.bean.panel.utils;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.PrimeFaces;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpatialUnitHelperServiceTest {

    @Mock
    private SpatialUnitService spatialUnitService;

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private SpatialUnitHelperService spatialUnitHelperService;

    private SpatialUnitHist spatialUnitHist;
    private SpatialUnit spatialUnit;
    private PrimeFaces mockPrimeFaces;

    @Mock
    private FacesContext facesContext;


    @BeforeEach
    void setUp() {
        spatialUnitHist = mock(SpatialUnitHist.class);
        spatialUnit = mock(SpatialUnit.class);

        // Mock PrimeFaces
        mockPrimeFaces = mock(PrimeFaces.class);
        PrimeFaces.setCurrent(mockPrimeFaces);

    }

    @Test
    void testVisualise() {
        Consumer<SpatialUnitHist> revisionSetter = mock(Consumer.class);
        spatialUnitHelperService.visualise(spatialUnitHist, revisionSetter);
        verify(revisionSetter).accept(spatialUnitHist);
    }

    @Test
    void testRestore() {
        // mock
        Mockito.doNothing().when(mockPrimeFaces).executeScript("PF('restored-dlg').show()");

        spatialUnitHelperService.restore(spatialUnitHist);
        verify(spatialUnitService, times(1)).restore(spatialUnitHist);
    }



    @Test
    void testFindHistory() {
        List<SpatialUnitHist> historyList = Collections.singletonList(spatialUnitHist);
        when(historyService.findSpatialUnitHistory(spatialUnit)).thenReturn(historyList);
        List<SpatialUnitHist> result = spatialUnitHelperService.findHistory(spatialUnit);
        assertEquals(historyList, result);
    }

    @Test
    void testReinitialize() {
        Consumer<SpatialUnit> spatialUnitSetter = mock(Consumer.class);
        Consumer<String> spatialUnitErrorMessageSetter = mock(Consumer.class);
        Consumer<String> spatialUnitListErrorMessageSetter = mock(Consumer.class);
        Consumer<String> recordingUnitListErrorMessageSetter = mock(Consumer.class);
        Consumer<String> actionUnitListErrorMessageSetter = mock(Consumer.class);
        Consumer<List<SpatialUnit>> spatialUnitListSetter = mock(Consumer.class);
        Consumer<List<RecordingUnit>> recordingUnitListSetter = mock(Consumer.class);
        Consumer<List<ActionUnit>> actionUnitListSetter = mock(Consumer.class);
        Consumer<List<SpatialUnit>> spatialUnitParentsListSetter = mock(Consumer.class);
        Consumer<String> spatialUnitParentsListErrorMessageSetter = mock(Consumer.class);

        spatialUnitHelperService.reinitialize(
                spatialUnitSetter,
                spatialUnitErrorMessageSetter,
                spatialUnitListErrorMessageSetter,
                recordingUnitListErrorMessageSetter,
                actionUnitListErrorMessageSetter,
                spatialUnitListSetter,
                recordingUnitListSetter,
                actionUnitListSetter,
                spatialUnitParentsListSetter,
                spatialUnitParentsListErrorMessageSetter
        );

        verify(spatialUnitSetter).accept(null);
        verify(spatialUnitErrorMessageSetter).accept(null);
        verify(spatialUnitListErrorMessageSetter).accept(null);
        verify(recordingUnitListErrorMessageSetter).accept(null);
        verify(actionUnitListErrorMessageSetter).accept(null);
        verify(spatialUnitListSetter).accept(null);
        verify(recordingUnitListSetter).accept(null);
        verify(actionUnitListSetter).accept(null);
        verify(spatialUnitParentsListSetter).accept(null);
        verify(spatialUnitParentsListErrorMessageSetter).accept(null);
    }
}
