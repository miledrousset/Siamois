package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Document;
import fr.siamois.models.Institution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {
    List<Document> findAllByArkIsNullAndCreatedByInstitution(Institution institution);
}
