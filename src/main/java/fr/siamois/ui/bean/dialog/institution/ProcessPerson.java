package fr.siamois.ui.bean.dialog.institution;

import jakarta.validation.constraints.NotNull;

public interface ProcessPerson {
    Boolean process(@NotNull PersonRole personRole);
}
