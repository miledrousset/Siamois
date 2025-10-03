package fr.siamois.infrastructure.database.initializer.seeder;

import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import fr.siamois.infrastructure.database.repositories.vocabulary.ConceptRepository;
import fr.siamois.infrastructure.database.repositories.vocabulary.label.ConceptLabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConceptSeeder {
    private final ConceptRepository conceptRepo;
    private final ConceptLabelRepository labelRepo;

    public record ConceptKey(String vocabularyExtId, String conceptExtId) {}

    public record ConceptSpec(String vocabularyId, String externalId, String label, String lang) {}
    public void seed(Vocabulary vocab, List<ConceptSpec> specs) {
        for (var s : specs) {
            var concept = conceptRepo.findConceptByExternalIdIgnoreCase(s.vocabularyId(), s.externalId())
                    .orElseGet(() -> {
                        var c = new Concept();
                        c.setExternalId(s.externalId());
                        c.setVocabulary(vocab);
                        return conceptRepo.save(c);
                    });
            var optLabel = labelRepo.findByConceptAndLangCode(concept, s.lang());
            if (optLabel.isEmpty()) {
                var l = new ConceptLabel();
                l.setConcept(concept);
                l.setValue(s.label());
                l.setLangCode(s.lang());
                labelRepo.save(l);
            }
        }
    }
}
