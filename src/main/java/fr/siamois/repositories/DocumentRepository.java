package fr.siamois.repositories;

import fr.siamois.models.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Integer> {

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN action_unit_document aud on s.document_id = aud.fk_document_id WHERE aud.fk_action_unit_id = :actionUnit"
    )
    List<Document> findAllDocumentsOfActionUnit(@Param("actionUnit") ActionUnit actionUnit);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN spatial_unit_document d ON s.document_id = d.fk_document_id WHERE d.fk_spatial_unit_id = :spatialUnit"
    )
    List<Document> findAllDocumentsOfSpatialUnit(@Param("spatialUnit")SpatialUnit spatialUnit);


    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN recording_unit_document d ON s.document_id = d.fk_document_id WHERE d.fk_recording_unit_id = :recordingUnit"
    )
    List<Document> findAllDocumentsOfRecordingUnit(@Param("recordingUnit") RecordingUnit recordingUnit);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN ru_study_document d ON s.document_id = d.fk_document_id WHERE d.fk_ru_study_id = :recordingUnitStudy"
    )
    List<Document> findAllDocumentsOfRecordingUnitStudy(@Param("recordingUnitStudy") RecordingUnitStudy recordingUnitStudy);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN specimen_document d ON s.document_id = d.fk_document_id WHERE d.fk_specimen_id = :specimen"
    )
    List<Document> findAllDocumentsOfSpecimen(@Param("specimen") Specimen specimen);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN specimen_study_document d ON s.document_id = d.fk_document_id WHERE d.fk_specimen_study_id = :specimenStudy"
    )
    List<Document> findAllDocumentsOfSpecimenStudy(@Param("specimenStudy") SpecimenStudy specimenStudy);
}
