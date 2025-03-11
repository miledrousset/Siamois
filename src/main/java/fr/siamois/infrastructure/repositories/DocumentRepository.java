package fr.siamois.infrastructure.repositories;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.document.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {
    List<Document> findAllByArkIsNullAndCreatedByInstitution(Institution institution);

    boolean existsByFileCode(String fileCode);

    Optional<Document> findByFileCode(String fileCode);
}
