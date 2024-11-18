package fr.siamois.repositories;

import fr.siamois.models.VocabularyType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyTypeRepository extends CrudRepository<VocabularyType, Long> {


}

