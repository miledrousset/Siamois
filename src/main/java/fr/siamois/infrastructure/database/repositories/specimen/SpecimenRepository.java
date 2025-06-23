package fr.siamois.infrastructure.database.repositories.specimen;

import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.specimen.Specimen;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecimenRepository extends CrudRepository<Specimen, Long> {
    List<Specimen> findAllByArkIsNullAndCreatedByInstitution(@NotNull Institution createdByInstitution);

    /**
     * Returns the maximum identifier given to a SPECIMEN in the context of an RECORDING UNIT
     *
     * @return The max identifier
     */
    @Query(
            nativeQuery = true,
            value = "SELECT MAX(s.identifier) " +
                    "FROM specimen s where s.fk_recording_unit_id = :recordingUnitId"
    )
    Integer findMaxUsedIdentifierByRecordingUnit(Long recordingUnitId);

}

