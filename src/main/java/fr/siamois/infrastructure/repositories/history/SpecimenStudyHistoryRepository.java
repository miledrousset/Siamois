package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.history.SpecimenStudyHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SpecimenStudyHistoryRepository extends CrudRepository<SpecimenStudyHist, Long>, HistoryEntries {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_specimen_study WHERE fk_author_id = :personId AND update_time BETWEEN :start AND :end"
    )
    List<SpecimenStudyHist> findAllOfUserBetween(OffsetDateTime start, OffsetDateTime end, Long personId);

}
