package fr.siamois.services;

import fr.siamois.infrastructure.repositories.history.GlobalHistoryRepository;
import fr.siamois.infrastructure.repositories.history.SpatialUnitHistoryRepository;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.models.history.HistoryOperation;
import fr.siamois.models.history.HistoryUpdateType;
import fr.siamois.models.history.SpatialUnitHist;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HistoryService {

    private final SpatialUnitHistoryRepository spatialUnitHistoryRepository;
    private final GlobalHistoryRepository globalHistoryRepository;

    private static final List<String> entityName = List.of("Action Unit", "Recording Unit", "Recording Unit Study",
            "Document", "Spatial Unit", "Specimen", "Specimen Study");

    private static final List<String> tableNames = List.of("action_unit", "recording_unit",
            "recording_unit_study", "siamois_document", "spatial_unit", "specimen", "specimen_study");

    public HistoryService(SpatialUnitHistoryRepository spatialUnitHistoryRepository, GlobalHistoryRepository globalHistoryRepository) {
        this.spatialUnitHistoryRepository = spatialUnitHistoryRepository;
        this.globalHistoryRepository = globalHistoryRepository;
    }


    public List<HistoryOperation> findAllOperationsOfUserAndTeamBetween(Person person, Team team, OffsetDateTime start, OffsetDateTime end) {
        if (start.isAfter(end)) return new ArrayList<>();

        List<HistoryOperation> operations = new ArrayList<>();
        addAllCreationOperations(person, team, operations, start, end);
        addAllHistoryOperations(person, team, operations, start, end);

        operations.sort(Comparator.comparing(HistoryOperation::actionDatetime).reversed());

        return operations;
    }

    private void addAllHistoryOperations(Person person, Team team, List<HistoryOperation> operations, OffsetDateTime beginTime, OffsetDateTime endTime) {
        for (int i = 0; i < entityName.size(); i++)
            addHistoryOperation(operations, tableNames.get(i), entityName.get(i), person, team, beginTime, endTime);
    }

    private void addHistoryOperation(List<HistoryOperation> operations, String tableName, String entityName, Person person, Team team, OffsetDateTime start, OffsetDateTime end) {
        globalHistoryRepository.findAllHistoryOfUserBetween("history_" + tableName, person, team, start, end).forEach((entry) ->
                operations.add(new HistoryOperation(entry.getUpdateType(),
                        entityName,
                        entry.getTableId(),
                        entry.getUpdateTime()))
                );
    }

    private void addCreateOperation(List<HistoryOperation> operations, String tableName, String entityName, Person person, Team team, OffsetDateTime start, OffsetDateTime end) {
        globalHistoryRepository.findAllCreationOfUserBetween(tableName, person, team,  start, end).forEach((entity) ->
                operations.add(new HistoryOperation(HistoryUpdateType.CREATE,
                        entityName,
                        entity.getId(),
                        entity.getCreationTime()))
                );
    }

    private void addAllCreationOperations(Person person, Team team, List<HistoryOperation> operations, OffsetDateTime beginTime, OffsetDateTime endTime) {
        for (int i = 0; i < entityName.size(); i++)
            addCreateOperation(operations, tableNames.get(i), entityName.get(i), person, team, beginTime, endTime);
    }

    public List<SpatialUnitHist> findSpatialUnitHistory(SpatialUnit current) {
        return spatialUnitHistoryRepository.findAllByTableId(current.getId());
    }

}
