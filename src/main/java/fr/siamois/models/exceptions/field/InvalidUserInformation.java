package fr.siamois.models.exceptions.field;

import lombok.Getter;

@Getter
public class InvalidUserInformation extends Exception {

    private final String userMessage;

    public InvalidUserInformation(String message) {
        super(message);
        userMessage = message;
    }

    public InvalidUserInformation(String systemMessage, String userMessage) {
        super(systemMessage);
        this.userMessage = userMessage;
    }

}
