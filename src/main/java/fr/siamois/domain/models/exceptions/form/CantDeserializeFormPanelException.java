package fr.siamois.domain.models.exceptions.form;

public class CantDeserializeFormPanelException extends RuntimeException  {

    public CantDeserializeFormPanelException(String message) {
        super(message);
    }
}
