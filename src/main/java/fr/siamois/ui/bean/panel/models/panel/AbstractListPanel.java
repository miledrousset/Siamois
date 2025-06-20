package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.services.BookmarkService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.LabelService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.utils.MessageUtils;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.component.api.UIColumn;
import org.primefaces.event.ColumnToggleEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.Visibility;
import org.primefaces.model.menu.DefaultMenuItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    protected final transient BookmarkService bookmarkService;

    // local
    protected BaseLazyDataModel<T> lazyDataModel;
    protected long totalNumberOfUnits;
    protected String errorMessage;
    protected transient List<T> selectedUnits ;

    protected AbstractListPanel(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;

        conceptService = null;
        langBean = null;
        spatialUnitService = null;
        personService = null;
        labelService = null;
        actionUnitService = null;
        sessionSettingsBean = null;
    }

    protected AbstractListPanel(SpatialUnitService spatialUnitService, PersonService personService, ConceptService conceptService, SessionSettingsBean sessionSettingsBean, LangBean langBean, LabelService labelService, ActionUnitService actionUnitService, BookmarkService bookmarkService) {

        this.spatialUnitService = spatialUnitService;
        this.personService = personService;
        this.conceptService = conceptService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.langBean = langBean;
        this.labelService = labelService;
        this.actionUnitService = actionUnitService;
        this.bookmarkService = bookmarkService;
    }

    public void onToggle(ColumnToggleEvent e) {
        Integer index = (Integer) e.getData();
        UIColumn column = e.getColumn();
        Visibility visibility = e.getVisibility();
        String header = column.getAriaHeaderText() != null ? column.getAriaHeaderText() : column.getHeaderText();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Column " + index + " toggled: " + header + " " + visibility, null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void handleRowSelect(SelectEvent<T> event) {
        FacesMessage msg = new FacesMessage("Row Selected");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void handleRowUnselect(UnselectEvent<T> event) {
        FacesMessage msg = new FacesMessage("Row Unselected");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

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
            ActionUnitService actionUnitService,
            BookmarkService bookmarkService) {

        super(titleKey, icon, cssClass);

        this.spatialUnitService = spatialUnitService;
        this.personService = personService;
        this.conceptService = conceptService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.langBean = langBean;
        this.labelService = labelService;
        this.actionUnitService = actionUnitService;
        this.bookmarkService = bookmarkService;
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

    public void bookmarkRow(String titleOrTitleCode, String ressourceUri) {

        // Maybe check that ressource exists and user has access to it?
        bookmarkService.save(
                sessionSettingsBean.getUserInfo(),
                ressourceUri,
                titleOrTitleCode
        );
        MessageUtils.displayInfoMessage(langBean, "common.bookmark.saved");
    }

    public Boolean isRessourceBookmarkedByUser(String ressourceUri) {
        return bookmarkService.isRessourceBookmarkedByUser(sessionSettingsBean.getUserInfo(), ressourceUri);
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
