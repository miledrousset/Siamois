package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.history.SpecimenHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface SpecimenHistoryRepository extends CrudRepository<SpecimenHist, Long>, HistoryEntries {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_specimen WHERE fk_author_id = :personId AND update_time BETWEEN :start AND :end"
    )
    List<SpecimenHist> findAllOfUserBetween(OffsetDateTime start, OffsetDateTime end, Long personId);

}
