package fr.siamois.ui.bean.panel.models.panel.single;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.ui.bean.dialog.document.DocumentCreationBean;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@Slf4j
public abstract class AbstractSingleEntityPanel<T, H> extends AbstractSingleEntity<T>  implements Serializable {

    // Deps
    protected final transient DocumentCreationBean documentCreationBean;

    //--------------- Locals

    protected int activeTabIndex; // Keeping state of active tab
    protected transient T backupClone;
    protected String errorMessage;
    protected transient List<H> historyVersion;
    protected transient H revisionToDisplay = null;
    protected Long idunit;  // ID of the spatial unit
    protected transient List<Document> documents;
    // lazy model for children of entity
    protected long totalChildrenCount = 0;
    protected transient List<Concept> selectedCategoriesChildren;

    protected abstract BaseLazyDataModel<T> getLazyDataModelChildren();

    // lazy model for parents of entity
    protected long totalParentsCount = 0;
    protected transient List<Concept> selectedCategoriesParents;

    public abstract BaseLazyDataModel<T> getLazyDataModelParents();

    // Gestion du formulaire via form layout
    protected CustomForm overviewForm;

    public abstract void init();

    public abstract List<Person> authorsAvailable();

    public static final Vocabulary SYSTEM_THESO;

    static {
        SYSTEM_THESO = new Vocabulary();
        SYSTEM_THESO.setBaseUri("https://thesaurus.mom.fr/");
        SYSTEM_THESO.setExternalVocabularyId("th230");
    }

    protected static final String COLUMN_CLASS_NAME = "ui-g-12 ui-md-6 ui-lg-4";

    protected AbstractSingleEntityPanel(String titleCodeOrTitle, String icon, String panelClass, DocumentCreationBean documentCreationBean,
                                        AbstractSingleEntity.Deps deps) {
        super(titleCodeOrTitle, icon, panelClass, deps);
        this.documentCreationBean = documentCreationBean;
    }


    public abstract void initForms();

    public abstract void cancelChanges();

    public abstract void visualise(H history);

    public abstract void saveDocument();

    public abstract void save(Boolean validated);


    public void initDialog() throws NoConfigForFieldException {
        log.trace("initDialog");
        documentCreationBean.init();
        documentCreationBean.setActionOnSave(this::saveDocument);

        PrimeFaces.current().executeScript("PF('newDocumentDiag').show()");
    }

    public Boolean isHierarchyTabEmpty() {
        return (totalChildrenCount + totalParentsCount) == 0;
    }


    public void onTabChange(TabChangeEvent<?> event) {
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
