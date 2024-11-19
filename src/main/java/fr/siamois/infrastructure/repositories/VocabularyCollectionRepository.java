package fr.siamois.infrastructure.repositories;

import fr.siamois.models.VocabularyCollection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyCollectionRepository extends CrudRepository<VocabularyCollection, Long> {
}
