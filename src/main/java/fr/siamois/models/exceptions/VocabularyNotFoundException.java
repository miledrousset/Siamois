package fr.siamois.models.exceptions;

public class VocabularyNotFoundException extends RuntimeException {
    public VocabularyNotFoundException(String message) {
        super(message);
    }
}
