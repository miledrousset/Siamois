package fr.siamois.infrastructure.repositories;

import fr.siamois.domain.models.Document;
import fr.siamois.domain.models.Institution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {
    List<Document> findAllByArkIsNullAndCreatedByInstitution(Institution institution);
}
