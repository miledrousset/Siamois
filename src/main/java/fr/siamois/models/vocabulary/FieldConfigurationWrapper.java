package fr.siamois.models.vocabulary;

import java.util.List;

public record FieldConfigurationWrapper(Vocabulary vocabularyConfig,
                                        List<VocabularyCollection> vocabularyCollectionsConfig) {

}
