package fr.siamois.ui.bean.panel.models.panel.single;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectOneFromFieldCode;
import fr.siamois.domain.models.form.customfield.CustomFieldText;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectOneFromFieldCode;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerText;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import fr.siamois.domain.services.spatialunit.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.services.form.CustomFieldService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.spatialunit.SpatialUnitTreeService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.LabelService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.document.DocumentCreationBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.panel.models.panel.single.tab.ActionTab;
import fr.siamois.ui.bean.panel.models.panel.single.tab.SpecimenTab;
import fr.siamois.ui.bean.panel.utils.SpatialUnitHelperService;
import fr.siamois.ui.lazydatamodel.ActionUnitInSpatialUnitLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitChildrenLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitParentsLazyDataModel;
import fr.siamois.utils.MessageUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import software.xdev.chartjs.model.charts.BarChart;
import software.xdev.chartjs.model.color.RGBAColor;
import software.xdev.chartjs.model.data.BarData;
import software.xdev.chartjs.model.dataset.BarDataset;
import software.xdev.chartjs.model.options.BarOptions;
import software.xdev.chartjs.model.options.Plugins;
import software.xdev.chartjs.model.options.Title;
import software.xdev.chartjs.model.options.Tooltip;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>This bean handles the spatial unit page</p>
 *
 * @author Grégory Bliault
 */
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SpatialUnitPanel extends AbstractSingleMultiHierarchicalEntityPanel<SpatialUnit, SpatialUnitHist> implements Serializable {

    // Dependencies
    private final transient RecordingUnitService recordingUnitService;
    private final transient SessionSettingsBean sessionSettings;
    private final transient SpatialUnitHelperService spatialUnitHelperService;
    private final transient CustomFieldService customFieldService;
    private final transient ConceptService conceptService;
    private final transient LabelService labelService;
    private final transient LangBean langBean;
    private final transient PersonService personService;


    private String spatialUnitErrorMessage;
    private transient List<SpatialUnit> spatialUnitList;
    private transient List<SpatialUnit> spatialUnitParentsList;
    private String spatialUnitListErrorMessage;
    private String spatialUnitParentsListErrorMessage;




    private transient List<CustomField> availableFields;
    private transient List<CustomField> selectedFields;

    // lazy model for children
    private SpatialUnitChildrenLazyDataModel lazyDataModelChildren ;
    // lazy model for parents
    private SpatialUnitParentsLazyDataModel lazyDataModelParents ;
    // Lazy model for actions in the spatial unit
    private ActionUnitInSpatialUnitLazyDataModel actionLazyDataModel;
    private long totalActionUnitCount;


    private String barModel;


    @Autowired
    private SpatialUnitPanel(RecordingUnitService recordingUnitService,
                             SessionSettingsBean sessionSettings,
                             SpatialUnitHelperService spatialUnitHelperService,
                             DocumentCreationBean documentCreationBean, CustomFieldService customFieldService,
                             ConceptService conceptService,
                             LabelService labelService, LangBean langBean, PersonService personService,
                             AbstractSingleEntity.Deps deps) {

        super("common.entity.spatialUnit", "bi bi-geo-alt", "siamois-panel spatial-unit-panel single-panel",
                documentCreationBean, deps);
        this.recordingUnitService = recordingUnitService;
        this.sessionSettings = sessionSettings;
        this.spatialUnitHelperService = spatialUnitHelperService;
        this.customFieldService = customFieldService;
        this.labelService = labelService;
        this.conceptService = conceptService;
        this.langBean = langBean;
        this.personService = personService;
    }


    public List<ConceptLabel> categoriesAvailable() {
        List<Concept> cList = conceptService.findAllBySpatialUnitOfInstitution(sessionSettings.getSelectedInstitution());
        return cList.stream()
                .map(concept -> labelService.findLabelOf(
                        concept, langBean.getLanguageCode()
                ))
                .toList();

    }

    @Override
    public String getAutocompleteClass() {
        return "spatial-unit-autocomplete";
    }

    @Override
    public String display() {
        return "/panel/recordingUnitPanel.xhtml";
    }

    @Override
    public String ressourceUri() {
        return "/spatial-unit/" + idunit;
    }

    @Override
    public String displayHeader() {
        return "/panel/header/spatialUnitPanelHeader.xhtml";
    }



    public void createBarModel() {
        barModel = new BarChart()
                .setData(new BarData()
                        .addDataset(new BarDataset()
                                .setData(65, 59, 80)
                                .setBackgroundColor(List.of(new RGBAColor(255, 99, 132, 0.5),new RGBAColor(12, 99, 132, 0.5),new RGBAColor(255, 17, 51, 0.5)))
                                .setBorderColor(new RGBAColor(255, 99, 132,1))
                                .setBorderWidth(1))
                        .setLabels("Hors contexte", "Unité stratigraphique", "Unité construite"))
                .setOptions(new BarOptions()
                        .setResponsive(true)
                        .setMaintainAspectRatio(false)
                        .setPlugins(new Plugins()
                                .setTooltip(new Tooltip().setMode("index"))
                                .setTitle(new Title()
                                        .setDisplay(true)
                                        .setText("Unités d'enregistrement (mockup)")
                                )
                        )
                ).toJson();
    }

    @Override
    public List<Person> authorsAvailable() {

        return personService.findAllAuthorsOfSpatialUnitByInstitution(sessionSettings.getSelectedInstitution());

    }

    @Override
    public void initForms() {

        overviewForm = SpatialUnit.OVERVIEW_FORM;
        detailsForm = SpatialUnit.DETAILS_FORM;
        // Init system form answers
        formResponse = initializeFormResponse(detailsForm, unit);
    }

    public void refreshUnit() {

        hasUnsavedModifications = false;
        unit = null;
        spatialUnitHelperService.reinitialize(
                unit -> this.unit = unit,
                msg -> this.spatialUnitErrorMessage = msg,
                msg -> this.spatialUnitListErrorMessage = msg,
                list -> this.spatialUnitList = list,
                list -> this.spatialUnitParentsList = list,
                msg -> this.spatialUnitParentsListErrorMessage = msg
        );

        try {

            this.unit = spatialUnitService.findById(idunit);
            this.setTitleCodeOrTitle(unit.getName()); // Set panel title

            backupClone = new SpatialUnit(unit);

            initForms();

            // Get direct parents and children counts

            // Fields for recording unit table
            selectedFields = new ArrayList<>();

            // Get all the CHILDREN of the spatial unit
            selectedCategoriesChildren = new ArrayList<>();
            lazyDataModelChildren= new SpatialUnitChildrenLazyDataModel(
                    spatialUnitService,
                    langBean,
                    unit
            );
            totalChildrenCount = spatialUnitService.countChildrenByParent(unit);

            // Get all the Parents of the spatial unit
            selectedCategoriesParents = new ArrayList<>();
            lazyDataModelParents = new SpatialUnitParentsLazyDataModel(
                    spatialUnitService,
                    langBean,
                    unit
            );
            totalParentsCount = spatialUnitService.countParentsByChild(unit);

            // Action in spatial unit lazy model
            actionLazyDataModel = new ActionUnitInSpatialUnitLazyDataModel(
                    actionUnitService,
                    sessionSettings,
                    langBean,
                    unit
            );
            totalActionUnitCount = actionUnitService.countBySpatialContext(unit);

        } catch (RuntimeException e) {
            this.spatialUnitErrorMessage = "Failed to load spatial unit: " + e.getMessage();
        }


        historyVersion = spatialUnitHelperService.findHistory(unit);
        documents = documentService.findForSpatialUnit(unit);
    }

    @Override
    public void cancelChanges() {
        unit.setGeom(backupClone.getGeom());
        unit.setName(backupClone.getName());
        unit.setValidated(backupClone.getValidated());
        unit.setArk(backupClone.getArk());
        unit.setCategory(backupClone.getCategory());
        hasUnsavedModifications = false;
        initForms();
    }

    @Override
    public void init() {

        createBarModel();

        if (idunit == null) {
            this.spatialUnitErrorMessage = "The ID of the spatial unit must be defined";
            return;
        }

        refreshUnit();

        super.init();

        ActionTab actionTab = new ActionTab(
                "common.entity.actionUnits",
                "bi bi-arrow-down-square",
                "actionTab",
                "recordingUnitForm:recordingUnitTabs",
                actionLazyDataModel);

        tabs.add(actionTab);

    }

    @Override
    public void visualise(SpatialUnitHist history) {
        spatialUnitHelperService.visualise(history, hist -> this.revisionToDisplay = hist);
    }

    public void restore(SpatialUnitHist history) {
        spatialUnitHelperService.restore(history);
        // refresh panel
        init();
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", history.getName());
    }



    public String getFormattedValue(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Number) {
            // Integer or Number case
            return value.toString();
        } else if (value instanceof List<?> list) {
            // Handle list of concepts
            String langCode = sessionSettings.getLanguageCode();
            return list.stream()
                    .map(item -> (item instanceof Concept concept) ? labelService.findLabelOf(concept, langCode).getValue() : item.toString())
                    .collect(Collectors.joining(", "));
        }

        return value.toString(); // Default case
    }

    @Override
    protected boolean documentExistsInUnitByHash(SpatialUnit unit, String hash) {
        return documentService.existInSpatialUnitByHash(unit, hash);
    }

    @Override
    protected void addDocumentToUnit(Document doc, SpatialUnit unit) {
        documentService.addToSpatialUnit(doc, unit);
    }




    @Override
    public void save(Boolean validated) {

        // Recupération des champs systeme

        // Name
        updateJpaEntityFromFormResponse(formResponse, unit);

        unit.setValidated(validated);
        try {
            spatialUnitService.save(unit);
        }
        catch(FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", unit.getName());
            return ;
        }

        refreshUnit();
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getName());
    }

    public static class SpatialUnitPanelBuilder {

        private final SpatialUnitPanel spatialUnitPanel;

        public SpatialUnitPanelBuilder(ObjectProvider<SpatialUnitPanel> spatialUnitPanelProvider) {
            this.spatialUnitPanel = spatialUnitPanelProvider.getObject();
        }

        public SpatialUnitPanelBuilder id(Long id) {
            spatialUnitPanel.setIdunit(id);
            return this;
        }

        public SpatialUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            spatialUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public SpatialUnitPanel build() {
            spatialUnitPanel.init();
            return spatialUnitPanel;
        }
    }

}