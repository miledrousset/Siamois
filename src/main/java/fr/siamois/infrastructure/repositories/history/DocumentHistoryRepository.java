package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.history.DocumentHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface DocumentHistoryRepository extends CrudRepository<DocumentHist, Long>, HistoryEntries {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_siamois_document WHERE fk_author_id = :personId AND update_time BETWEEN :start AND :end"
    )
    List<DocumentHist> findAllOfUserBetween(OffsetDateTime start, OffsetDateTime end, Long personId);

}
