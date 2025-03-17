package fr.siamois.infrastructure.database.repositories.history;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.history.GlobalHistoryEntry;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface GlobalHistoryRepository {
    List<GlobalHistoryEntry> findAllHistoryOfUserBetween(String tableName, UserInfo userInfo, OffsetDateTime start, OffsetDateTime end) throws SQLException;
    List<TraceableEntity> findAllCreationOfUserBetween(String tableName, UserInfo userInfo, OffsetDateTime start, OffsetDateTime end) throws SQLException;
}
