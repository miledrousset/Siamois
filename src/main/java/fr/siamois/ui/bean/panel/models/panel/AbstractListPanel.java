package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.LabelService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.menu.DefaultMenuItem;

import java.util.ArrayList;
import java.util.HashSet;

@Getter
@Setter
public abstract class AbstractListPanel<T> extends AbstractPanel {

    // deps
    protected final transient SpatialUnitService spatialUnitService;
    protected final transient PersonService personService;
    protected final transient ConceptService conceptService;
    protected final transient SessionSettingsBean sessionSettingsBean;
    protected final transient LangBean langBean;
    protected final transient LabelService labelService;
    protected final transient ActionUnitService actionUnitService;

    // local
    protected BaseLazyDataModel<T> lazyDataModel;
    protected long totalNumberOfUnits;

    protected AbstractListPanel(
            String titleKey,
            String icon,
            String cssClass,
            SpatialUnitService spatialUnitService,
            PersonService personService,
            ConceptService conceptService,
            SessionSettingsBean sessionSettingsBean,
            LangBean langBean,
            LabelService labelService,
            ActionUnitService actionUnitService) {

        super(titleKey, icon, cssClass);

        this.spatialUnitService = spatialUnitService;
        this.personService = personService;
        this.conceptService = conceptService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.langBean = langBean;
        this.labelService = labelService;
        this.actionUnitService = actionUnitService;
    }

    protected abstract long countUnitsByInstitution();

    protected abstract BaseLazyDataModel<T> createLazyDataModel();

    protected void configureLazyDataModel(BaseLazyDataModel<T> model) {
        model.setSortBy(new HashSet<>());
        model.setFirst(0);
        model.setPageSizeState(5);
        model.setSelectedAuthors(new ArrayList<>());
        model.setSelectedTypes(new ArrayList<>());
        model.setNameFilter("");
        model.setGlobalFilter("");
    }

    protected abstract void setErrorMessage(String msg);


    public void init() {

        DefaultMenuItem item = DefaultMenuItem.builder()
                .value(langBean.msg(getBreadcrumbKey()))
                .icon(getBreadcrumbIcon())
                .build();

        if (isBreadcrumbVisible()) {
            this.getBreadcrumb().getModel().getElements().add(item);
        }

        totalNumberOfUnits = countUnitsByInstitution();
        lazyDataModel = createLazyDataModel();
        configureLazyDataModel(lazyDataModel);

    }

    protected abstract String getBreadcrumbKey();

    protected abstract String getBreadcrumbIcon();

    @Override
    public abstract String displayHeader();
}
