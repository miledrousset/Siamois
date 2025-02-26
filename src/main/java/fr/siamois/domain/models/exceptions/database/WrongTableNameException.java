package fr.siamois.domain.models.exceptions.database;

import java.sql.SQLException;

public class WrongTableNameException extends SQLException {
    public WrongTableNameException(String message) {
        super(message);
    }
}
