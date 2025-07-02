package fr.siamois.domain.services;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.history.*;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.infrastructure.database.repositories.history.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service for managing history operations.
 * This service provides methods to retrieve history operations for various entities
 * such as Action Unit, Recording Unit, Spatial Unit, and Specimen.
 */
@Slf4j
@Service
public class HistoryService {

    private final SpatialUnitHistoryRepository spatialUnitHistoryRepository;
    private final ActionUnitHistoryRepository actionUnitHistoryRepository;
    private final RecordingUnitHistoryRepository recordingUnitHistoryRepository;
    private final SpecimenHistoryRepository specimenHistoryRepository;
    private final GlobalHistoryRepository globalHistoryRepository;

    private static final List<String> entityName = List.of("Action Unit", "Recording Unit", "Recording Unit Study",
            "Document", "Spatial Unit", "Specimen", "Specimen Study");

    private static final List<String> tableNames = List.of("action_unit", "recording_unit",
            "recording_unit_study", "siamois_document", "spatial_unit", "specimen", "specimen_study");

    public HistoryService(SpatialUnitHistoryRepository spatialUnitHistoryRepository, ActionUnitHistoryRepository actionUnitHistoryRepository, RecordingUnitHistoryRepository recordingUnitHistoryRepository, SpecimenHistoryRepository specimenHistoryRepository, GlobalHistoryRepository globalHistoryRepository) {
        this.spatialUnitHistoryRepository = spatialUnitHistoryRepository;
        this.actionUnitHistoryRepository = actionUnitHistoryRepository;
        this.recordingUnitHistoryRepository = recordingUnitHistoryRepository;
        this.specimenHistoryRepository = specimenHistoryRepository;
        this.globalHistoryRepository = globalHistoryRepository;
    }

    /**
     * Finds all history operations of a user and team between specified start and end times.
     *
     * @param info  the user information
     * @param start the start time of the period
     * @param end   the end time of the period
     * @return a list of history operations
     * @throws SQLException if there is an error accessing the database
     */
    public List<HistoryOperation> findAllOperationsOfUserAndTeamBetween(UserInfo info, OffsetDateTime start, OffsetDateTime end) throws SQLException {
        if (start.isAfter(end)) return new ArrayList<>();

        List<HistoryOperation> operations = new ArrayList<>();
        addAllCreationOperations(info, operations, start, end);
        addAllHistoryOperations(info, operations, start, end);

        operations.sort(Comparator.comparing(HistoryOperation::actionDatetime).reversed());

        return operations;
    }

    private void addAllHistoryOperations(UserInfo info, List<HistoryOperation> operations, OffsetDateTime beginTime, OffsetDateTime endTime) throws SQLException {
        for (int i = 0; i < entityName.size(); i++)
            addHistoryOperation(operations, tableNames.get(i), entityName.get(i), info, beginTime, endTime);
    }

    private void addHistoryOperation(List<HistoryOperation> operations, String tableName, String entityName, UserInfo info, OffsetDateTime start, OffsetDateTime end) throws SQLException {
        globalHistoryRepository.findAllHistoryOfUserBetween("history_" + tableName, info, start, end).forEach(entry ->
                operations.add(new HistoryOperation(entry.getUpdateType(),
                        entityName,
                        entry.getTableId(),
                        entry.getUpdateTime()))
        );
    }

    private void addCreateOperation(List<HistoryOperation> operations, String tableName, String entityName, UserInfo info, OffsetDateTime start, OffsetDateTime end) throws SQLException {
        globalHistoryRepository.findAllCreationOfUserBetween(tableName, info, start, end).forEach(entity ->
                operations.add(new HistoryOperation(HistoryUpdateType.CREATE,
                        entityName,
                        entity.getId(),
                        entity.getCreationTime()))
        );
    }

    private void addAllCreationOperations(UserInfo info, List<HistoryOperation> operations, OffsetDateTime beginTime, OffsetDateTime endTime) throws SQLException {
        for (int i = 0; i < entityName.size(); i++)
            addCreateOperation(operations, tableNames.get(i), entityName.get(i), info, beginTime, endTime);
    }

    /**
     * Finds the history of a Spatial Unit.
     *
     * @param current the current Spatial Unit
     * @return a list of SpatialUnitHist objects representing the history of the Spatial Unit
     */
    public List<SpatialUnitHist> findSpatialUnitHistory(SpatialUnit current) {
        return spatialUnitHistoryRepository.findAllByTableId(current.getId());
    }

    /**
     * Finds the history of an Action Unit.
     *
     * @param current the current Action Unit
     * @return a list of ActionUnitHist objects representing the history of the Action Unit
     */
    public List<ActionUnitHist> findActionUnitHistory(ActionUnit current) {
        return actionUnitHistoryRepository.findAllByTableId(current.getId());
    }

    /**
     * Finds the history of a Recording Unit.
     *
     * @param current the current Recording Unit
     * @return a list of RecordingUnitHist objects representing the history of the Recording Unit
     */
    public List<RecordingUnitHist> findRecordingUnitHistory(RecordingUnit current) {
        return recordingUnitHistoryRepository.findAllByTableId(current.getId());
    }

    /**
     * Finds the history of a Specimen.
     *
     * @param current the current Specimen
     * @return a list of SpecimenHist objects representing the history of the Specimen
     */
    public List<SpecimenHist> findSpecimenHistory(Specimen current) {
        return specimenHistoryRepository.findAllByTableId(current.getId());
    }

}
