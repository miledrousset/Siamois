package fr.siamois.ui.bean.dialog.newunit;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.specimen.Specimen;

public enum UnitKind {
    SPATIAL(
            "/spatial-unit/new",
            "Nouvelle unité spatiale",
            "spatial-unit-panel",
            "bi bi-pencil-square",
            "spatial-unit-autocomplete",
            "common.entity.spatialUnits.updated",
            "/spatial-unit/",
            SpatialUnit.NEW_UNIT_FORM

    ),
    ACTION(
            "/action-unit/new",
            "Nouvelle unité d'action",
            "action-unit-panel",
            "bi bi-arrow-down-square",
            "action-unit-autocomplete",
            "common.entity.spatialUnits.updated",
            "/action-unit/",
            ActionUnit.NEW_UNIT_FORM
    ),
    SPECIMEN(
            "/specimen/new",
            "Nouveau prélèvement",
            "specimen-panel",
            "bi bi-box-2e",
            "specimen-autocomplete",
            "common.entity.specimen.updated",
            "/specimen/",
            Specimen.NEW_UNIT_FORM
    ),
    RECORDING(
            "/recording-unit/new",
            "Nouvelle unité d'enregistrement",
            "recording-unit-panel",
            "bi bi-pencil-square",
            "recording-unit-autocomplete",
            "common.entity.recordingUnits.updated",
            "/recording-unit/",
            RecordingUnit.NEW_UNIT_FORM
    );

    private final String resourceUri;
    private final String title;
    private final String styleClass;
    private final String icon;
    private final String autocompleteClass;
    private final String successMessageCode;
    private final String urlPrefix;
    private final CustomForm customForm;


    UnitKind(String resourceUri,
             String title,
             String styleClass,
             String icon,
             String autocompleteClass,
             String successMessageCode,
             String url,
             CustomForm customForm) {
        this.resourceUri = resourceUri;
        this.title = title;
        this.styleClass = styleClass;
        this.icon = icon;
        this.autocompleteClass = autocompleteClass;
        this.successMessageCode = successMessageCode;
        this.urlPrefix = url;
        this.customForm = customForm;
    }

    public String resourceUri() {
        return resourceUri;
    }

    public String title() {
        return title;
    }

    public String styleClass() {
        return styleClass;
    }

    public String icon() {
        return icon;
    }

    public String autocompleteClass() {
        return autocompleteClass;
    }

    public CustomForm formLayout() {
        return customForm;
    }

    public String viewUrlFor(Long id) {
        return urlPrefix + id;
    }

    public String successMessageCode() {
        return successMessageCode;
    }
}
