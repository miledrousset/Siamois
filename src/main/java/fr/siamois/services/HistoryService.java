package fr.siamois.services;

import fr.siamois.infrastructure.repositories.ActionUnitRepository;
import fr.siamois.infrastructure.repositories.DocumentRepository;
import fr.siamois.infrastructure.repositories.SpatialUnitRepository;
import fr.siamois.infrastructure.repositories.history.*;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitStudyRepository;
import fr.siamois.infrastructure.repositories.specimen.SpecimenRepository;
import fr.siamois.infrastructure.repositories.specimen.SpecimenStudyRepository;
import fr.siamois.models.auth.Person;
import fr.siamois.models.history.HistoryOperation;
import fr.siamois.models.history.HistoryUpdateType;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HistoryService {

    private final ActionUnitRepository actionUnitRepository;
    private final RecordingUnitRepository recordingUnitRepository;
    private final RecordingUnitStudyRepository recordingUnitStudyRepository;
    private final DocumentRepository documentRepository;
    private final SpatialUnitRepository spatialUnitRepository;
    private final SpecimenRepository specimenRepository;
    private final SpecimenStudyRepository specimenStudyRepository;
    private final ActionUnitHistoryEntries actionUnitHistoryRepository;
    private final RecordingUnitHistoryEntries recordingUnitHistoryRepository;
    private final RecordingUnitStudyHistoryRepository recordingUnitStudyHistoryRepository;
    private final DocumentHistoryRepository documentHistoryRepository;
    private final SpatialUnitHistoryRepository spatialUnitHistoryRepository;
    private final SpecimenHistoryRepository specimenHistoryRepository;
    private final SpecimenStudyHistoryRepository specimenStudyHistoryRepository;

    public HistoryService(ActionUnitRepository actionUnitRepository, RecordingUnitRepository recordingUnitRepository, RecordingUnitStudyRepository recordingUnitStudyRepository, DocumentRepository documentRepository, SpatialUnitRepository spatialUnitRepository, SpecimenRepository specimenRepository, SpecimenStudyRepository specimenStudyRepository, ActionUnitHistoryEntries actionUnitHistoryRepository, RecordingUnitHistoryEntries recordingUnitHistoryRepository, RecordingUnitStudyHistoryRepository recordingUnitStudyHistoryRepository, DocumentHistoryRepository documentHistoryRepository, SpatialUnitHistoryRepository spatialUnitHistoryRepository, SpecimenHistoryRepository specimenHistoryRepository, SpecimenStudyHistoryRepository specimenStudyHistoryRepository) {
        this.actionUnitRepository = actionUnitRepository;
        this.recordingUnitRepository = recordingUnitRepository;
        this.recordingUnitStudyRepository = recordingUnitStudyRepository;
        this.documentRepository = documentRepository;
        this.spatialUnitRepository = spatialUnitRepository;
        this.specimenRepository = specimenRepository;
        this.specimenStudyRepository = specimenStudyRepository;
        this.actionUnitHistoryRepository = actionUnitHistoryRepository;
        this.recordingUnitHistoryRepository = recordingUnitHistoryRepository;
        this.recordingUnitStudyHistoryRepository = recordingUnitStudyHistoryRepository;
        this.documentHistoryRepository = documentHistoryRepository;
        this.spatialUnitHistoryRepository = spatialUnitHistoryRepository;
        this.specimenHistoryRepository = specimenHistoryRepository;
        this.specimenStudyHistoryRepository = specimenStudyHistoryRepository;
    }

    public List<HistoryOperation> findAllOperationsOfUserBetween(Person person, OffsetDateTime start, OffsetDateTime end) {
        if (start.isAfter(end)) return new ArrayList<>();

        List<HistoryOperation> operations = new ArrayList<>();
        addAllCreationOperations(person, operations, start, end);
        addAllHistoryOperations(person, operations, start, end);

        operations.sort(Comparator.comparing(HistoryOperation::actionDatetime).reversed());

        return operations;
    }

    private void addAllHistoryOperations(Person person, List<HistoryOperation> operations, OffsetDateTime beginTime, OffsetDateTime endTime) {
        addHistoryOperation(actionUnitHistoryRepository, operations, "Action Unit", person, beginTime, endTime);
        addHistoryOperation(recordingUnitHistoryRepository, operations, "Recording Unit", person, beginTime, endTime);
        addHistoryOperation(recordingUnitStudyHistoryRepository, operations, "Recording Unit Study", person, beginTime, endTime);
        addHistoryOperation(documentHistoryRepository, operations, "Document", person, beginTime, endTime);
        addHistoryOperation(spatialUnitHistoryRepository, operations, "Spatial Unit", person, beginTime, endTime);
        addHistoryOperation(specimenHistoryRepository, operations, "Specimen", person, beginTime, endTime);
        addHistoryOperation(specimenStudyHistoryRepository, operations, "Specimen Study", person, beginTime, endTime);
    }


    private void addHistoryOperation(HistoryEntries repository, List<HistoryOperation> operations, String entityName, Person person, OffsetDateTime start, OffsetDateTime end) {
        repository.findAllOfUserBetween(start, end, person.getId()).forEach((historyEntry) ->
                operations.add(new HistoryOperation(historyEntry.getUpdateType(),
                        entityName,
                        historyEntry.getTableId(),
                        historyEntry.getUpdateTime())));
    }

    private void addCreateOperation(TraceableEntries repository, List<HistoryOperation> operations, String entityName, Person person, OffsetDateTime start, OffsetDateTime end) {
        repository.findAllCreatedBetweenByUser(start, end, person.getId()).forEach((traceableEntity ->
                operations.add(new HistoryOperation(HistoryUpdateType.CREATE,
                        entityName,
                        traceableEntity.getId(),
                        traceableEntity.getCreationTime()))));
    }

    private void addAllCreationOperations(Person person, List<HistoryOperation> operations, OffsetDateTime beginTime, OffsetDateTime endTime) {
        addCreateOperation(actionUnitRepository, operations, "Action Unit", person, beginTime, endTime);
        addCreateOperation(recordingUnitRepository, operations, "Recording Unit", person, beginTime, endTime);
        addCreateOperation(recordingUnitStudyRepository, operations, "Recording Unit Study", person, beginTime, endTime);
        addCreateOperation(documentRepository, operations, "Document", person, beginTime, endTime);
        addCreateOperation(spatialUnitRepository, operations, "Spatial Unit", person, beginTime, endTime);
        addCreateOperation(specimenRepository, operations, "Specimen", person, beginTime, endTime);
        addCreateOperation(specimenStudyRepository, operations, "Specimen Study", person, beginTime, endTime);
    }

}
