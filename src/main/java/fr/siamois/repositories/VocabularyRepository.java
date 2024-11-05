package fr.siamois.repositories;

import fr.siamois.models.Vocabulary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyRepository extends CrudRepository<Vocabulary, Integer> {


}

