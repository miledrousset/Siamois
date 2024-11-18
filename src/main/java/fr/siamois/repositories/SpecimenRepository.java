package fr.siamois.repositories;

import fr.siamois.models.Specimen;
import fr.siamois.models.SpecimenGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecimenRepository extends CrudRepository<Specimen, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM specimen s JOIN specimen_group_attribution sga ON s.specimen_id = sga.fk_specimen_id WHERE sga.fk_specimen_group_id = :specimenGroup"
    )
    List<Specimen> findAllSpecimensOfSpecimenGroup(@Param("specimenGroup") SpecimenGroup specimenGroup);

}

