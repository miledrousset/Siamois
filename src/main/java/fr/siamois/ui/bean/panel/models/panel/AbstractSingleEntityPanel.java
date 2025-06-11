package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitChildrenLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitParentsLazyDataModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractSingleEntityPanel<T,HType> extends AbstractPanel {

    //--------------- Locals
    protected transient T unit;
    protected Boolean hasUnsavedModifications ; // Did we modify the spatial unit?
    protected int activeTabIndex ; // Keeping state of active tab
    protected transient T backupClone;
    protected transient List<HType> historyVersion;
    protected transient HType revisionToDisplay = null;
    protected Long idunit;  // ID of the spatial unit
    protected List<Document> documents;
    // lazy model for children of entity
    protected long totalChildrenCount = 0;
    protected List<Concept> selectedCategoriesChildren;
    protected abstract BaseLazyDataModel<T> getLazyDataModelChildren() ;
    // lazy model for parents of entity
    protected long totalParentsCount = 0;
    protected List<Concept> selectedCategoriesParents;
    public abstract BaseLazyDataModel<T> getLazyDataModelParents() ;

    protected static final String COLUMN_CLASS_NAME = "ui-g-12 ui-md-6 ui-lg-4";

    protected AbstractSingleEntityPanel() {
        super();
    }

    protected AbstractSingleEntityPanel(String titleCodeOrTitle, String icon, String panelClass) {
        super(titleCodeOrTitle, icon, panelClass);
    }

    public abstract void init();

    public abstract List<Person> authorsAvailable();

    public abstract void initForms();

    public abstract void cancelChanges();

    public abstract void saveDocument();

    public abstract void save(Boolean validated);

    protected void onTabChange(TabChangeEvent event) {
        // update tab inddex
        TabView tabView = (TabView) event.getComponent(); // Get the TabView
        Tab activeTab = event.getTab(); // Get the selected tab

        int index = activeTabIndex;
        List<Tab> tabs = tabView.getChildren().stream()
                .filter(Tab.class::isInstance)
                .map(Tab.class::cast)
                .toList();

        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).equals(activeTab)) {
                index = i;
                break;
            }
        }

        activeTabIndex = index;
    }




}
