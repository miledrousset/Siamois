package fr.siamois.infrastructure.database.repositories.vocabulary.label;

import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConceptLabelRepository extends CrudRepository<ConceptLabel, Long> {
    Optional<ConceptLabel> findByConceptAndLangCode(Concept concept, String langCode);

    List<ConceptLabel> findAllByConcept(Concept concept);
}
