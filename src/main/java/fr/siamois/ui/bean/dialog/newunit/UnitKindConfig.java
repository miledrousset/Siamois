package fr.siamois.ui.bean.dialog.newunit;

import fr.siamois.domain.models.form.customform.CustomForm;

public record UnitKindConfig(
        String resourceUri,
        String title,
        String styleClass,
        String icon,
        String autocompleteClass,
        String successMessageCode,
        String urlPrefix,
        CustomForm customForm
) {}
