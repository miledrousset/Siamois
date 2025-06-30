package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitChildrenLazyDataModel;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.*;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NewActionUnitPanel extends AbstractPanel {

    // Deps
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient SpatialUnitService spatialUnitService;
    private final transient ActionUnitService actionUnitService;
    private final FlowBean flowBean;
    private final transient FieldConfigurationService fieldConfigurationService;

    private Map<SpatialUnit, Boolean> spatialUnitIsInAction;
    private Map<SpatialUnit, List<SpatialUnit>> neighborMap;

    // Locals
    ActionUnit actionUnit;
    Long spatialUnitId;
    BaseLazyDataModel<?> lazyDataModel;


    public NewActionUnitPanel(LangBean langBean, SessionSettingsBean sessionSettingsBean, SpatialUnitService spatialUnitService, ActionUnitService actionUnitService, FlowBean flowBean, FieldConfigurationService fieldConfigurationService) {
        super("Nouvelle unité d'action", "bi bi-arrow-down-square", "siamois-panel action-unit-panel new-action-unit-panel");
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.flowBean = flowBean;
        this.fieldConfigurationService = fieldConfigurationService;
    }


    public void generateRandomActionUnitIdentifier() {
        actionUnit.setIdentifier("2025");
    }

    @Override
    public String display() {
        return "/panel/newActionUnitPanel.xhtml";
    }

    @Override
    public String ressourceUri() {
        return String.format("/actionunit/%s/new", spatialUnitId);
    }

    void init() {
        actionUnit = new ActionUnit();
        spatialUnitIsInAction = new HashMap<>();
        neighborMap = spatialUnitService.neighborMapOfAllSpatialUnit(sessionSettingsBean.getSelectedInstitution());

        if(spatialUnitId != null) {
            SpatialUnit spatialUnitById = spatialUnitService.findById(spatialUnitId);
            actionUnit.getSpatialContext().add(spatialUnitById);
            spatialUnitIsInAction.put(spatialUnitById, true);
        }
        // Set spatial context based on lazy model type
        else if (lazyDataModel instanceof SpatialUnitChildrenLazyDataModel typedModel) {
            SpatialUnit spatialUnitByTypedModel = spatialUnitService.findById(typedModel.getSpatialUnit().getId());
            actionUnit.getSpatialContext().add(spatialUnitByTypedModel);
            spatialUnitIsInAction.put(spatialUnitByTypedModel, true);
        }

        for (SpatialUnit su : neighborMap.keySet()) {
            spatialUnitIsInAction.putIfAbsent(su, false);
        }

        DefaultMenuItem item = DefaultMenuItem.builder()
                .value("Nouvelle unité d'action")
                .icon("bi bi-arrow-down-square")
                .build();
        this.getBreadcrumb().getModel().getElements().add(item);
    }

    /**
     * Fetch the autocomplete results on API for the type field and add them to the list of concepts.
     *
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<Concept> completeActionUnitType(String input) {
        UserInfo info = sessionSettingsBean.getUserInfo();
        List<Concept> concepts = Collections.emptyList();
        try {
            concepts = fieldConfigurationService.fetchAutocomplete(info, ActionUnit.TYPE_FIELD_CODE, input);
        } catch (NoConfigForFieldException e) {
            log.error(e.getMessage(), e);
        }
        return concepts;
    }

    public String getUrlForActionUnitTypeFieldCode() {
        return fieldConfigurationService.getUrlForFieldCode(sessionSettingsBean.getUserInfo(), ActionUnit.TYPE_FIELD_CODE);
    }


    public boolean save() {
        try {
            Person author = sessionSettingsBean.getAuthenticatedUser();
            actionUnit.setAuthor(author);
            actionUnit.setBeginDate(OffsetDateTime.now());
            actionUnit.setEndDate(OffsetDateTime.now());
            actionUnit.setValidated(false);

            ActionUnit saved = actionUnitService.save(sessionSettingsBean.getUserInfo() ,actionUnit, actionUnit.getType());

            Integer idx = flowBean.getPanels().indexOf(this);

            // If unable to find idx, we create new panel?

            // Remove last item from breadcrumb
            this.getBreadcrumb().getModel().getElements().remove(this.getBreadcrumb().getModel().getElements().size() - 1);
            flowBean.goToActionUnitByIdCurrentPanel(saved.getId(), idx);

            for (Map.Entry<SpatialUnit, Boolean> entry : spatialUnitIsInAction.entrySet()) {
                if (entry.getValue()) {
                    actionUnit.getSpatialContext().add(entry.getKey());
                }
            }

            flowBean.updateHomePanel();

            return true;

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("actionunit.creationfailed", this.actionUnit.getName())));

            log.error("Error while saving: {}", e.getMessage());
            return false;
        }
    }

    public TreeNode<SpatialUnit> rootNodeOfSpatialUnits() {
        TreeNode<SpatialUnit> root = new DefaultTreeNode<>(new SpatialUnit(), null);
        Map<SpatialUnit, TreeNode<SpatialUnit>> nodesMap = new HashMap<>();

        for (Map.Entry<SpatialUnit, List<SpatialUnit>> entry : neighborMap.entrySet()) {
            TreeNode<SpatialUnit> parent = nodesMap.computeIfAbsent(entry.getKey(), k -> new CheckboxTreeNode<>(entry.getKey()));
            for (SpatialUnit child : entry.getValue()) {
                TreeNode<SpatialUnit> childNode = nodesMap.computeIfAbsent(child, k -> new CheckboxTreeNode<>(child, parent));
                parent.getChildren().add(childNode);
            }
        }

        for (SpatialUnit spatialUnit : neighborMap.keySet()) {
            if (numberOfParentsOf(neighborMap, spatialUnit) == 0) {
                root.getChildren().add(nodesMap.get(spatialUnit));
            }
        }

        return root;
    }

    private long numberOfParentsOf(Map<SpatialUnit, List<SpatialUnit>> neighborMap, SpatialUnit spatialUnit) {
        long count = 0;
        for (Map.Entry<SpatialUnit, List<SpatialUnit>> entry : neighborMap.entrySet()) {
            for (SpatialUnit child : entry.getValue()) {
                if (child.getId().equals(spatialUnit.getId()))
                    count++;
            }
        }
        return count;
    }

    public void changeValueFor(SpatialUnit spatialUnit) {
        boolean newValue = !spatialUnitIsInAction.getOrDefault(spatialUnit, false);
        spatialUnitIsInAction.put(spatialUnit, newValue);
        for (SpatialUnit child : neighborMap.get(spatialUnit)) {
            spatialUnitIsInAction.put(child, newValue);
        }
    }

    public static class NewActionUnitPanelBuilder {

        private final NewActionUnitPanel newActionUnitPanel;

        public NewActionUnitPanelBuilder(ObjectProvider<NewActionUnitPanel> newActionUnitPanelProvider) {
            this.newActionUnitPanel = newActionUnitPanelProvider.getObject();
        }

        public NewActionUnitPanel.NewActionUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            newActionUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public NewActionUnitPanel.NewActionUnitPanelBuilder lazyModel(BaseLazyDataModel<?> lazyModel) {
            newActionUnitPanel.setLazyDataModel(lazyModel);

            return this;
        }

        public NewActionUnitPanel.NewActionUnitPanelBuilder spatialUnitId(Long id) {
            newActionUnitPanel.setSpatialUnitId(id);

            return this;
        }

        public NewActionUnitPanel build() {
            newActionUnitPanel.init();
            return newActionUnitPanel;
        }
    }
}
