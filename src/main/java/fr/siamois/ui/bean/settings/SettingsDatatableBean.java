package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.Institution;

import java.io.Serializable;

public interface SettingsDatatableBean extends Serializable {
    void init(Institution institution);
    void add();
    void filter();
    String getSearchInput();
    void setSearchInput(String searchInput);
}
