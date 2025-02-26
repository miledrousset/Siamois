package fr.siamois.domain.models.exceptions;

public class VocabularyNotFoundException extends RuntimeException {
    public VocabularyNotFoundException(String message) {
        super(message);
    }
}
