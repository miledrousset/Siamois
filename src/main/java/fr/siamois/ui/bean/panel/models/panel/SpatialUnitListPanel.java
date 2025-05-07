package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.LabelService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.model.SpatialUnitLazyDataModel;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.primefaces.component.api.UIColumn;
import org.primefaces.event.ColumnToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.Visibility;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;


import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SpatialUnitListPanel extends AbstractPanel {

    private final transient SpatialUnitService spatialUnitService;
    private final transient PersonService personService;
    private final transient ConceptService conceptService;
    private final SessionSettingsBean sessionSettingsBean;
    private final LangBean langBean;
    private final transient LabelService labelService;

    // locals
    private String spatialUnitListErrorMessage;
    private List<Concept> selectedCategories;
    private List<Person> selectedAuthors;
    private LazyDataModel<SpatialUnit> lazyDataModel ;
    private long totalNumberOfUnits ;

    public void onToggle(ColumnToggleEvent e) {
        Integer index = (Integer) e.getData();
        UIColumn column = e.getColumn();
        Visibility visibility = e.getVisibility();
        String header = column.getAriaHeaderText() != null ? column.getAriaHeaderText() : column.getHeaderText();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Column " + index + " toggled: " + header + " " + visibility, null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public SpatialUnitListPanel(SpatialUnitService spatialUnitService, PersonService personService,
                                ConceptService conceptService,
                                SessionSettingsBean sessionSettingsBean, LangBean langBean, LabelService labelService) {
        super("panel.title.allspatialunit", "bi bi-geo-alt", "siamois-panel spatial-unit-panel spatial-unit-list-panel");
        this.spatialUnitService = spatialUnitService;
        this.personService = personService;
        this.conceptService = conceptService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.langBean = langBean;
        this.labelService = labelService;
    }

    @Override
    public String displayHeader() {
        return "/panel/header/spatialUnitListPanelHeader.xhtml";
    }


    public void init()  {
        try {
            // Add current item to breadcrumb
            DefaultMenuItem item = DefaultMenuItem.builder()
                    .value("Unités géographiques")
                    .icon("bi bi-geo-alt")
                    .build();
            this.getBreadcrumb().getModel().getElements().add(item);
            // Get all the spatial unit within the institution
            selectedCategories = new ArrayList<>();
            selectedAuthors = new ArrayList<>();
            totalNumberOfUnits = spatialUnitService.countByInstitution(sessionSettingsBean.getSelectedInstitution());
            lazyDataModel = new SpatialUnitLazyDataModel(
                    spatialUnitService,
                    sessionSettingsBean,
                    langBean
            );

        } catch (RuntimeException e) {
            spatialUnitListErrorMessage = "Failed to load spatial units: " + e.getMessage();
        }
    }

    public List<ConceptLabel> categoriesAvailable() {
        List<Concept> cList = conceptService.findAllConceptsByInstitution(sessionSettingsBean.getSelectedInstitution());

        return cList.stream()
                .map(concept -> labelService.findLabelOf(
                        concept, langBean.getLanguageCode()
                ))
                .toList();

    }

    public List<Person> authorsAvailable() {

        return personService.findAllAuthorsOfSpatialUnitByInstitution(sessionSettingsBean.getSelectedInstitution());

    }



    @Override
    public String display() {
        return "/panel/spatialUnitListPanel.xhtml";
    }

    public static class SpatialUnitListPanelBuilder {

        private final SpatialUnitListPanel spatialUnitListPanel;

        public SpatialUnitListPanelBuilder(ObjectProvider<SpatialUnitListPanel> spatialUnitListPanelProvider) {
            this.spatialUnitListPanel = spatialUnitListPanelProvider.getObject();
        }

        public SpatialUnitListPanel.SpatialUnitListPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            spatialUnitListPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public SpatialUnitListPanel build() {
            spatialUnitListPanel.init();
            return spatialUnitListPanel;
        }
    }





}
