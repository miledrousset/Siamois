package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.LabelService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.model.ActionUnitLazyDataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.primefaces.model.SortMeta;

import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ActionUnitListPanel extends AbstractPanel {

    private final transient SpatialUnitService spatialUnitService;
    private final transient PersonService personService;
    private final transient ConceptService conceptService;
    private final SessionSettingsBean sessionSettingsBean;
    private final LangBean langBean;
    private final transient LabelService labelService;
    private final transient ActionUnitService actionUnitService;

    // locals
    private String actionUnitListErrorMessage;
    private ActionUnitLazyDataModel lazyDataModel ;
    private long totalNumberOfUnits ;

    private Set<SortMeta> sortBy = new HashSet<>();

    // Filters
    private transient List<ConceptLabel> selectedTypes = new ArrayList<>();
    private transient List<ConceptLabel> selectedAuthors = new ArrayList<>();
    private String nameFilter;
    private String globalFilter;


    public ActionUnitListPanel(SpatialUnitService spatialUnitService, PersonService personService,
                               ConceptService conceptService,
                               SessionSettingsBean sessionSettingsBean, LangBean langBean, LabelService labelService, ActionUnitService actionUnitService) {


        super("panel.title.allactionunit", "bi bi-arrow-down-square", "siamois-panel action-unit-panel action-unit-list-panel");

        this.spatialUnitService = spatialUnitService;
        this.personService = personService;
        this.conceptService = conceptService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.langBean = langBean;
        this.labelService = labelService;
        this.actionUnitService = actionUnitService;
    }

    @Override
    public String displayHeader() {
        return "/panel/header/actionUnitListPanelHeader.xhtml";
    }

    public void init()  {
        try {
            // Add current item to breadcrumb
            DefaultMenuItem item = DefaultMenuItem.builder()
                    .value(langBean.msg("common.entity.actionUnits"))
                    .icon("bi bi-arrow-down-square")
                    .build();
            this.getBreadcrumb().getModel().getElements().add(item);



            totalNumberOfUnits = actionUnitService.countByInstitution(sessionSettingsBean.getSelectedInstitution());

            // init lazy model
            lazyDataModel = new ActionUnitLazyDataModel(
                    actionUnitService,
                    sessionSettingsBean,
                    langBean
            );
            lazyDataModel.setSortBy(sortBy);
            lazyDataModel.setFirst(0);
            lazyDataModel.setPageSizeState(5);
            lazyDataModel.setSelectedAuthors(new ArrayList<>());
            lazyDataModel.setSelectedTypes(new ArrayList<>());
            lazyDataModel.setNameFilter("");
            lazyDataModel.setGlobalFilter("");



        } catch (RuntimeException e) {
            actionUnitListErrorMessage = "Failed to load action units: " + e.getMessage();
        }
    }

    public List<ConceptLabel> categoriesAvailable() {
        List<Concept> cList = conceptService.findAllByActionUnitOfInstitution(sessionSettingsBean.getSelectedInstitution());

        return cList.stream()
                .map(concept -> labelService.findLabelOf(
                        concept, langBean.getLanguageCode()
                ))
                .toList();

    }

    public List<Person> authorsAvailable() {

        return personService.findAllAuthorsOfActionUnitByInstitution(sessionSettingsBean.getSelectedInstitution());

    }



    @Override
    public String display() {
        return "/panel/actionUnitListPanel.xhtml";
    }

    public static class ActionUnitListPanelBuilder {

        private final ActionUnitListPanel actionUnitListPanel;

        public ActionUnitListPanelBuilder(ObjectProvider<ActionUnitListPanel> actionUnitListPanelProvider) {
            this.actionUnitListPanel = actionUnitListPanelProvider.getObject();
        }

        public ActionUnitListPanel.ActionUnitListPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            actionUnitListPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public ActionUnitListPanel build() {
            actionUnitListPanel.init();
            return actionUnitListPanel;
        }
    }





}
