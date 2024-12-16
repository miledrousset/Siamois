package fr.siamois.models.vocabulary;

import java.util.List;

/**
 * Wrapper for the configuration of a field.
 * @param vocabularyConfig Contains a vocabulary configuration if the configuration is a vocabulary based. The vocabularyCollectionConfig should be null.
 * @param vocabularyCollectionsConfig Contains a list of vocabulary collections if the configuration is a vocabulary collection based. The vocabularyConfig should be null.
 */
public record FieldConfigurationWrapper(Vocabulary vocabularyConfig,
                                        List<VocabularyCollection> vocabularyCollectionsConfig) {

}
