package fr.siamois.domain.models.exceptions.api;

public class NotSiamoisThesaurusException extends Exception {

    public NotSiamoisThesaurusException(String s, Object ...o) {
        super(String.format(s, o));
    }

}
