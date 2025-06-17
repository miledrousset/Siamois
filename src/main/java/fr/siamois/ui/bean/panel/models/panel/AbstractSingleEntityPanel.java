package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import jakarta.faces.component.UIComponent;
import jakarta.faces.event.AjaxBehaviorEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractSingleEntityPanel<T,H> extends AbstractPanel {

    //--------------- Locals
    protected transient T unit;
    protected Boolean hasUnsavedModifications ; // Did we modify the spatial unit?
    protected int activeTabIndex ; // Keeping state of active tab
    protected transient T backupClone;
    protected String errorMessage;
    protected transient List<H> historyVersion;
    protected transient H revisionToDisplay = null;
    protected Long idunit;  // ID of the spatial unit
    protected transient List<Document> documents;
    // lazy model for children of entity
    protected long totalChildrenCount = 0;
    protected transient List<Concept> selectedCategoriesChildren;
    protected abstract BaseLazyDataModel<T> getLazyDataModelChildren() ;
    // lazy model for parents of entity
    protected long totalParentsCount = 0;
    protected transient List<Concept> selectedCategoriesParents;
    public abstract BaseLazyDataModel<T> getLazyDataModelParents() ;
    // Gestion du formulaire via form layout
    protected transient List<CustomFormPanel> layout ; // details tab form
    protected transient List<CustomFormPanel> overviewLayout ; // overview tab form
    protected CustomFormResponse formResponse ; // answers to all the fields from overview and details
    protected Vocabulary systemTheso ;

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

    public Boolean isHierarchyTabEmpty () {
        return (totalChildrenCount + totalParentsCount) == 0;
    }

    public void setFieldAnswerHasBeenModified(CustomField field) {

        formResponse.getAnswers().get(field).setHasBeenModified(true);
        hasUnsavedModifications = true;

    }

    public void setFieldConceptAnswerHasBeenModified(AjaxBehaviorEvent event) {
        UIComponent component = event.getComponent();
        CustomField field = (CustomField) component.getAttributes().get("field");

        formResponse.getAnswers().get(field).setHasBeenModified(true);
        hasUnsavedModifications = true;
    }

    public void onTabChange(TabChangeEvent event) {
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
