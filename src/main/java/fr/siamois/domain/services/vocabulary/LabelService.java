package fr.siamois.domain.services.vocabulary;

import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import fr.siamois.domain.models.vocabulary.label.VocabularyLabel;
import fr.siamois.infrastructure.database.repositories.vocabulary.label.ConceptLabelRepository;
import fr.siamois.infrastructure.database.repositories.vocabulary.label.VocabularyLabelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LabelService {

    private final ConceptLabelRepository conceptLabelRepository;
    private final VocabularyLabelRepository vocabularyLabelRepository;

    public LabelService(ConceptLabelRepository conceptLabelRepository, VocabularyLabelRepository vocabularyLabelRepository) {
        this.conceptLabelRepository = conceptLabelRepository;
        this.vocabularyLabelRepository = vocabularyLabelRepository;
    }

    public ConceptLabel findLabelOf(Concept concept, String langCode) {
        Optional<ConceptLabel> label = conceptLabelRepository.findByConceptAndLangCode(concept, langCode);
        if (label.isPresent())
            return label.get();

        List<ConceptLabel> allLabels = conceptLabelRepository.findAllByConcept(concept);
        if (allLabels.isEmpty()) {
            ConceptLabel defaultLabel = new ConceptLabel();
            defaultLabel.setLangCode(langCode);
            defaultLabel.setConcept(concept);
            defaultLabel.setValue(concept.getExternalId());

            return defaultLabel;
        }

        return allLabels.get(0);
    }

    public VocabularyLabel findLabelOf(Vocabulary vocabulary, String langCode) {
        Optional<VocabularyLabel> label = vocabularyLabelRepository.findByVocabularyAndLangCode(vocabulary, langCode);
        if (label.isPresent())
            return label.get();

        List<VocabularyLabel> allLabels = vocabularyLabelRepository.findAllByVocabulary(vocabulary);
        if (allLabels.isEmpty()) {
            VocabularyLabel defaultLabel = new VocabularyLabel();
            defaultLabel.setLangCode(langCode);
            defaultLabel.setVocabulary(vocabulary);
            defaultLabel.setValue(vocabulary.getExternalVocabularyId());

            return defaultLabel;
        }

        return allLabels.get(0);
    }

    public ConceptLabel updateLabel(Concept concept, String langCode, String value) {
        ConceptLabel label = new ConceptLabel();
        label.setLangCode(langCode);
        label.setValue(value);
        label.setConcept(concept);
        return conceptLabelRepository.save(label);
    }

    public VocabularyLabel updateLabel(Vocabulary vocabulary, String langCode, String value) {
        Optional<VocabularyLabel> existingLabelOpt = vocabularyLabelRepository.findByVocabularyAndLangCode(vocabulary, langCode);
        if (existingLabelOpt.isEmpty()) {
            VocabularyLabel label = new VocabularyLabel();
            label.setLangCode(langCode);
            label.setValue(value);
            label.setVocabulary(vocabulary);
            return vocabularyLabelRepository.save(label);
        }

        VocabularyLabel existingLabel = existingLabelOpt.get();

        if (existingLabel.getValue() == null || !existingLabel.getValue().equals(value)) {
            existingLabel.setValue(value);
            return vocabularyLabelRepository.save(existingLabel);
        }

        return existingLabel;
    }

}
