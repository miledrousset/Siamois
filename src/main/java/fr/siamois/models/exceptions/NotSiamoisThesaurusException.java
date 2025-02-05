package fr.siamois.models.exceptions;

public class NotSiamoisThesaurusException extends Exception {

    public NotSiamoisThesaurusException(String s, Object ...o) {
        super(String.format(s, o));
    }

}
