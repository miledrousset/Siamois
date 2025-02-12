package fr.siamois.services;

import fr.siamois.infrastructure.repositories.history.GlobalHistoryRepository;
import fr.siamois.infrastructure.repositories.history.RecordingUnitHistoryRepository;
import fr.siamois.infrastructure.repositories.history.SpatialUnitHistoryRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.TraceableEntity;
import fr.siamois.models.UserInfo;
import fr.siamois.models.auth.Person;
import fr.siamois.models.history.GlobalHistoryEntry;
import fr.siamois.models.history.HistoryOperation;
import fr.siamois.models.history.HistoryUpdateType;
import fr.siamois.models.history.SpatialUnitHist;
import fr.siamois.models.spatialunit.SpatialUnit;
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
    private GlobalHistoryRepository globalHistoryRepository;

    private HistoryService historyService;
    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        historyService = new HistoryService(spatialUnitHistoryRepository, recordingUnitHistoryRepository, globalHistoryRepository);
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
}