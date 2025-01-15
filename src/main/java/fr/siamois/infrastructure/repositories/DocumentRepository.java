package fr.siamois.infrastructure.repositories;

import fr.siamois.infrastructure.repositories.history.TraceableEntries;
import fr.siamois.models.Document;
import fr.siamois.models.recordingunit.RecordingUnitStudy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long>, TraceableEntries {

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN action_unit_document aud on s.document_id = aud.fk_document_id WHERE aud.fk_action_unit_id = :actionUnitId"
    )
    List<Document> findAllDocumentsOfActionUnit(@Param("actionUnitId") Long actionUnitId);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN spatial_unit_document d ON s.document_id = d.fk_document_id WHERE d.fk_spatial_unit_id = :spatialUnitId"
    )
    List<Document> findAllDocumentsOfSpatialUnit(@Param("spatialUnitId")Long spatialUnitId);


    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN recording_unit_document d ON s.document_id = d.fk_document_id WHERE d.fk_recording_unit_id = :recordingUnitId"
    )
    List<Document> findAllDocumentsOfRecordingUnit(@Param("recordingUnitId") Long recordingUnitId);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN ru_study_document d ON s.document_id = d.fk_document_id WHERE d.fk_ru_study_id = :recordingUnitStudyId"
    )
    List<Document> findAllDocumentsOfRecordingUnitStudy(@Param("recordingUnitStudyId") Long recordingUnitStudyId);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN specimen_document d ON s.document_id = d.fk_document_id WHERE d.fk_specimen_id = :specimenId"
    )
    List<Document> findAllDocumentsOfSpecimen(@Param("specimenId") Long specimenId);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM siamois_document s JOIN specimen_study_document d ON s.document_id = d.fk_document_id WHERE d.fk_specimen_study_id = :specimenStudyId"
    )
    List<Document> findAllDocumentsOfSpecimenStudy(@Param("specimenStudyId") Long specimenStudyId);

    @Query(
            nativeQuery = true,
            value = "SELECT doc.* FROM siamois_document doc WHERE fk_author_id = :author AND creation_time BETWEEN :start AND :end"
    )
    List<Document> findAllCreatedBetweenByUser(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, @Param("author") Long personId);
}
