package fr.siamois.domain.models.exceptions.vocabulary;

public class VocabularyNotFoundException extends RuntimeException {
    public VocabularyNotFoundException(String message) {
        super(message);
    }
}
