package fr.siamois.infrastructure.database.repositories;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.document.Document;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {
    List<Document> findAllByArkIsNullAndCreatedByInstitution(Institution institution);

    boolean existsByFileCode(String fileCode);

    Optional<Document> findByFileCode(String fileCode);

    @Query(
            nativeQuery = true,
            value = "SELECT d.* FROM siamois_document d " +
                    "JOIN spatial_unit_document sud ON d.document_id = sud.fk_document_id " +
                    "WHERE sud.fk_spatial_unit_id = :spatialUnitId"
    )
    List<Document> findDocumentsBySpatialUnit(Long spatialUnitId);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO spatial_unit_document(fk_document_id, fk_spatial_unit_id) " +
                    "VALUES (:documentId, :spatialUnitId)"
    )
    void addDocumentToSpatialUnit(Long documentId, Long spatialUnitId);

    @Query(
            nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM spatial_unit_document sud " +
                    "JOIN siamois_document sd ON sud.fk_document_id = sd.document_id " +
                    "WHERE sud.fk_spatial_unit_id = :spatialUnitId AND sd.md5_sum = :hash"
    )
    boolean existsByHashInSpatialUnit(Long spatialUnitId, String hash);
}
