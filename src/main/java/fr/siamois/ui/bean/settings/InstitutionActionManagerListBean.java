package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.institution.Institution;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;

@Getter
@Setter
@Component
@SessionScoped
public class InstitutionActionManagerListBean implements SettingsDatatableBean {

    private Institution institution;

    public void reset() {
        this.institution = null;
    }

    public void init(Institution institution) {
        this.institution = institution;
    }

    @Override
    public void add() {

    }

    @Override
    public void filter() {

    }

    @Override
    public String getSearchInput() {
        return "";
    }

    @Override
    public void setSearchInput(String searchInput) {

    }
}
