package fr.siamois.services;

import fr.siamois.infrastructure.repositories.ActionUnitRepository;
import fr.siamois.infrastructure.repositories.DocumentRepository;
import fr.siamois.infrastructure.repositories.SpatialUnitRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitStudyRepository;
import fr.siamois.infrastructure.repositories.specimen.SpecimenRepository;
import fr.siamois.infrastructure.repositories.specimen.SpecimenStudyRepository;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.TraceableEntity;
import fr.siamois.models.auth.Person;
import fr.siamois.models.history.HistoryOperation;
import fr.siamois.models.history.HistoryUpdateType;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.RecordingUnitStudy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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

    public HistoryService(ActionUnitRepository actionUnitRepository, RecordingUnitRepository recordingUnitRepository, RecordingUnitStudyRepository recordingUnitStudyRepository, DocumentRepository documentRepository, SpatialUnitRepository spatialUnitRepository, SpecimenRepository specimenRepository, SpecimenStudyRepository specimenStudyRepository) {
        this.actionUnitRepository = actionUnitRepository;
        this.recordingUnitRepository = recordingUnitRepository;
        this.recordingUnitStudyRepository = recordingUnitStudyRepository;
        this.documentRepository = documentRepository;
        this.spatialUnitRepository = spatialUnitRepository;
        this.specimenRepository = specimenRepository;
        this.specimenStudyRepository = specimenStudyRepository;
    }

    public List<HistoryOperation> findAllOperationsOfUserBetween(Person person, OffsetDateTime start, OffsetDateTime end) {
        if (start.isAfter(end)) return new ArrayList<>();

        List<HistoryOperation> operations = new ArrayList<>();
        addAllCreationOperations(person, operations, start, end);

        return operations;
    }


    private void addAllCreationOperations(Person person, List<HistoryOperation> operations, OffsetDateTime beginTime, OffsetDateTime endTime) {
        actionUnitRepository.findAllCreatedBetweenByUser(beginTime, endTime, person.getId()).forEach((actionUnit ->
                operations.add(new HistoryOperation(HistoryUpdateType.CREATE,
                        "Action Unit",
                        actionUnit.getId(),
                        actionUnit.getCreationTime()))));
        recordingUnitRepository.findAllCreatedBetweenByUser(beginTime, endTime, person.getId()).forEach((recordingUnit ->
                operations.add(new HistoryOperation(HistoryUpdateType.CREATE,
                        "Recording Unit",
                        recordingUnit.getId(),
                        recordingUnit.getCreationTime()))));
        recordingUnitStudyRepository.findAllCreatedBetweenByUser(beginTime, endTime, person.getId()).forEach((rus) ->
                operations.add(new HistoryOperation(HistoryUpdateType.CREATE,
                        "Recording Unit Study",
                        rus.getId(),
                        rus.getCreationTime())));
        documentRepository.findAllCreatedBetweenByUser(beginTime, endTime, person.getId()).forEach((doc) ->
                operations.add(new HistoryOperation(HistoryUpdateType.CREATE,
                        "Document",
                        doc.getId(),
                        doc.getCreationTime())));
        spatialUnitRepository.findAllCreatedBetweenByUser(beginTime, endTime, person.getId()).forEach((su) ->
                operations.add(new HistoryOperation(HistoryUpdateType.CREATE,
                        "Spatial Unit",
                        su.getId(),
                        su.getCreationTime())));
        specimenRepository.findAllCreatedBetweenByUser(beginTime, endTime, person.getId()).forEach((s) -> {
            operations.add(new HistoryOperation(
                    HistoryUpdateType.CREATE,
                    "Specimen",
                    s.getId(),
                    s.getCreationTime()
            ));
        });
        specimenStudyRepository.findAllCreatedBetweenByUser(beginTime, endTime, person.getId()).forEach((s) -> {
            operations.add(new HistoryOperation(
                    HistoryUpdateType.CREATE,
                    "Specimen",
                    s.getId(),
                    s.getCreationTime()
            ));
        });
    }

}
