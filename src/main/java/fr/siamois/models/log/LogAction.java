package fr.siamois.models.log;

public enum LogAction {

    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private final String value;

    LogAction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
