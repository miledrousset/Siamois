package fr.siamois.ui.bean.dialog.institution;

import jakarta.validation.constraints.NotNull;

public interface ProcessPerson {
    void process(@NotNull PersonRole personRole);
}
