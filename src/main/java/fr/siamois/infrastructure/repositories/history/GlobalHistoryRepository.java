package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.TraceableEntity;
import fr.siamois.models.UserInfo;
import fr.siamois.models.history.GlobalHistoryEntry;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface GlobalHistoryRepository {
    List<GlobalHistoryEntry> findAllHistoryOfUserBetween(String tableName, UserInfo userInfo, OffsetDateTime start, OffsetDateTime end) throws SQLException;
    List<TraceableEntity> findAllCreationOfUserBetween(String tableName, UserInfo userInfo, OffsetDateTime start, OffsetDateTime end) throws SQLException;
}
