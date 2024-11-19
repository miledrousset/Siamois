package fr.siamois.infrastructure.repositories;

import fr.siamois.models.VocabularyType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabularyTypeRepository extends CrudRepository<VocabularyType, Long> {

    Optional<VocabularyType> findVocabularyTypeByLabel(String label);

}

