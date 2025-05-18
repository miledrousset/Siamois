package fr.siamois.ui.bean.settings;

import java.io.Serializable;

public interface SettingsDatatableBean extends Serializable {
    void add();
    void filter();
    String getSearchInput();
    void setSearchInput(String searchInput);
}
