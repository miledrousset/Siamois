package fr.siamois.ui.bean.panel.utils;

import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Data
@Service
public class SpatialUnitHelperService {

    private final SpatialUnitService spatialUnitService;
    private final HistoryService historyService;

    public SpatialUnitHelperService(SpatialUnitService spatialUnitService, HistoryService historyService) {
        this.spatialUnitService = spatialUnitService;
        this.historyService = historyService;
    }

    public void visualise(SpatialUnitHist history, Consumer<SpatialUnitHist> revisionSetter) {
        log.trace("History version changed to {}", history);
        revisionSetter.accept(history);
    }

    public void restore(SpatialUnitHist history) {
        log.trace("Restore order received");
        spatialUnitService.restore(history);
        PrimeFaces.current().executeScript("PF('restored-dlg').show()");
    }

    public List<SpatialUnitHist> findHistory(SpatialUnit spatialUnit) {
        return historyService.findSpatialUnitHistory(spatialUnit);
    }

}
