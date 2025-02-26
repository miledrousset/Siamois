package fr.siamois.domain.models.exceptions.api;

import org.springframework.http.HttpStatusCode;

public class ClientSideErrorException extends Exception {

    public ClientSideErrorException(String msg, HttpStatusCode code) {
        super(String.format("Error %s : %s", code.toString(), msg));
    }

}
