package fr.siamois.ui.bean.panel.utils;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.utils.MessageUtils;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
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

        //PrimeFaces.current().executeScript("PF('restored-dlg').show()");
    }



    public <T> List<T> getFirstThree(Set<T> itemSet) {
        if(itemSet == null) {
            return new ArrayList<>();
        }
        else if (itemSet.size() <= 3) {
            return new ArrayList<>(itemSet);
        } else {
            List<T> tempList = new ArrayList<>(itemSet);
            return tempList.subList(0, 3);
        }
    }

    public List<SpatialUnitHist> findHistory(SpatialUnit spatialUnit) {
        return historyService.findSpatialUnitHistory(spatialUnit);
    }

    public void reinitialize(Consumer<SpatialUnit> spatialUnitSetter,
                             Consumer<String> spatialUnitErrorMessageSetter,
                             Consumer<String> spatialUnitListErrorMessageSetter,
                             Consumer<String> recordingUnitListErrorMessageSetter,
                             Consumer<String> actionUnitListErrorMessageSetter,
                             Consumer<List<SpatialUnit>> spatialUnitListSetter,
                             Consumer<List<RecordingUnit>> recordingUnitListSetter,
                             Consumer<List<ActionUnit>> actionUnitListSetter,
                             Consumer<List<SpatialUnit>> spatialUnitParentsListSetter,
                             Consumer<String> spatialUnitParentsListErrorMessageSetter) {

        spatialUnitSetter.accept(null);
        spatialUnitErrorMessageSetter.accept(null);
        spatialUnitListErrorMessageSetter.accept(null);
        recordingUnitListErrorMessageSetter.accept(null);
        actionUnitListErrorMessageSetter.accept(null);
        spatialUnitListSetter.accept(null);
        recordingUnitListSetter.accept(null);
        actionUnitListSetter.accept(null);
        spatialUnitParentsListSetter.accept(null);
        spatialUnitParentsListErrorMessageSetter.accept(null);
    }

}

