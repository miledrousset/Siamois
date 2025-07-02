package fr.siamois.domain.services;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.history.*;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.infrastructure.database.repositories.history.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private SpatialUnitHistoryRepository spatialUnitHistoryRepository;

    @Mock
    private RecordingUnitHistoryRepository recordingUnitHistoryRepository;

    @Mock
    private ActionUnitHistoryRepository actionUnitHistoryRepository;

    @Mock
    private SpecimenHistoryRepository specimenHistoryRepository;

    @Mock
    private GlobalHistoryRepository globalHistoryRepository;

    private HistoryService historyService;
    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        historyService = new HistoryService(spatialUnitHistoryRepository,
                actionUnitHistoryRepository,
                recordingUnitHistoryRepository,
                specimenHistoryRepository,
                globalHistoryRepository);
        setupUserInfo();
    }

    private void setupUserInfo() {
        userInfo = new UserInfo(new Institution(), new Person(), "fr");
        userInfo.getInstitution().setId(-1L);
        userInfo.getInstitution().setName("Name");
        userInfo.getUser().setId(-1L);
        userInfo.getUser().setUsername("Username");
    }

    private static class TraceableEntityImpl extends TraceableEntity {

        private final Long id;

        public TraceableEntityImpl(long l, OffsetDateTime now) {
            super();
            id = l;
            creationTime = now;
        }

        @Override
        public Long getId() {
            return id;
        }
    }

    @Test
    void findAllOperationsOfUserAndTeamBetween() throws SQLException {
        OffsetDateTime start = OffsetDateTime.now().minusDays(1);
        OffsetDateTime end = OffsetDateTime.now();

        List<GlobalHistoryEntry> historyEntries = new ArrayList<>();
        historyEntries.add(new GlobalHistoryEntry(HistoryUpdateType.UPDATE, 1L, OffsetDateTime.now()));

        List<TraceableEntity> creationEntries = new ArrayList<>();
        creationEntries.add(new TraceableEntityImpl(1L, OffsetDateTime.now()));

        when(globalHistoryRepository.findAllHistoryOfUserBetween(anyString(), eq(userInfo), eq(start), eq(end)))
                .then(invocation -> {
                    if (invocation.getArgument(0, String.class).equals("history_action_unit"))
                        return historyEntries;
                    return List.of();
                });

        when(globalHistoryRepository.findAllCreationOfUserBetween(anyString(), eq(userInfo), eq(start), eq(end)))
                .then(invocation -> {
                    if (invocation.getArgument(0, String.class).equals("action_unit"))
                        return creationEntries;
                    return List.of();
                });

        List<HistoryOperation> operations = historyService.findAllOperationsOfUserAndTeamBetween(userInfo, start, end);

        assertEquals(2, operations.size());
        verify(globalHistoryRepository, times(7)).findAllHistoryOfUserBetween(anyString(), eq(userInfo), eq(start), eq(end));
        verify(globalHistoryRepository, times(7)).findAllCreationOfUserBetween(anyString(), eq(userInfo), eq(start), eq(end));
    }

    @Test
    void findAllOperationsOfUserAndTeamBetween_whenInvalidDate_shouldReturnEmptyList() throws SQLException {
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = start.minusDays(2);
        List<HistoryOperation> result = historyService.findAllOperationsOfUserAndTeamBetween(userInfo, start, end);
        assertThat(result).isEmpty();
    }

    @Test
    void findSpatialUnitHistory() {
        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setId(1L);

        List<SpatialUnitHist> spatialUnitHists = new ArrayList<>();
        spatialUnitHists.add(new SpatialUnitHist());

        when(spatialUnitHistoryRepository.findAllByTableId(1L)).thenReturn(spatialUnitHists);

        List<SpatialUnitHist> result = historyService.findSpatialUnitHistory(spatialUnit);

        assertEquals(1, result.size());
        verify(spatialUnitHistoryRepository, times(1)).findAllByTableId(1L);
    }

    @Test
    void findRecordingUnitHistory() {
        RecordingUnit recordingUnit = new RecordingUnit();
        recordingUnit.setId(1L);

        List<RecordingUnitHist> recordingUnitHists = new ArrayList<>();
        recordingUnitHists.add(new RecordingUnitHist());

        when(recordingUnitHistoryRepository.findAllByTableId(1L)).thenReturn(recordingUnitHists);

        List<RecordingUnitHist> result = historyService.findRecordingUnitHistory(recordingUnit);

        assertEquals(1, result.size());
        verify(recordingUnitHistoryRepository, times(1)).findAllByTableId(1L);
    }

}