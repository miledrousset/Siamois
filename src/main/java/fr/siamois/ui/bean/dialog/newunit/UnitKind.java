package fr.siamois.ui.bean.dialog.newunit;

public enum UnitKind {
    SPATIAL(
            "/spatial-unit/new",
            "Nouvelle unité spatiale",
            "spatial-unit-panel",
            "bi bi-pencil-square",
            "spatial-unit-autocomplete"
    ),
    ACTION(
            "/action-unit/new",
            "Nouvelle unité d'action",
            "action-unit-panel",
            "bi bi-arrow-down-square",
            "action-unit-autocomplete"
    ),
    SPECIMEN(
            "/specimen/new",
            "Nouveau prélèvement",
            "specimen-panel",
            "bi bi-box-2e",
            "specimen-autocomplete"
    ),
    RECORDING(
            "/recording-unit/new",
                    "Nouvelle unité d'enregistrement",
                    "recording-unit-panel",
                    "bi bi-pencil-square",
                    "recording-unit-autocomplete"
    );

    private final String resourceUri;
    private final String title;
    private final String styleClass;
    private final String icon;
    private final String autocompleteClass;

    UnitKind(String resourceUri, String title, String styleClass, String icon, String autocompleteClass) {
        this.resourceUri = resourceUri;
        this.title = title;
        this.styleClass = styleClass;
        this.icon = icon;
        this.autocompleteClass = autocompleteClass;
    }
    public String resourceUri()        { return resourceUri; }
    public String title()              { return title; }
    public String styleClass()         { return styleClass; }
    public String icon()               { return icon; }
    public String autocompleteClass()  { return autocompleteClass; }
}
