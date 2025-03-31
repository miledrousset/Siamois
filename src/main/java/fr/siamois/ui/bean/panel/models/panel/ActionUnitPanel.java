package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionCode;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.exceptions.actionunit.ActionUnitNotFoundException;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.services.form.CustomFieldService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.domain.utils.DocumentUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.DocumentCreationBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.panel.utils.DataLoaderUtils;
import fr.siamois.ui.bean.panel.utils.SpatialUnitHelperService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>This bean handles the spatial unit page</p>
 *
 * @author Grégory Bliault
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ActionUnitPanel extends AbstractPanel implements Serializable {

    // Deps
    private final transient ActionUnitService actionUnitService;
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient FieldService fieldService;
    private final RedirectBean redirectBean;


    // Local
    private ActionUnit actionUnit;
    private String actionUnitErrorMessage;
    private Long id;  // ID of the action unit requested

    // For entering new code
    private ActionCode newCode;
    private Integer newCodeIndex; // Index of the new code, if primary: 0, otherwise 1 to N
    // (but corresponds to 0 to N-1 in secondary code list)

    // Field related
    private Boolean editType;
    private Concept fType;

    private transient List<ActionCode> secondaryActionCodes;


    public ActionUnitPanel(ActionUnitService actionUnitService, LangBean langBean, SessionSettingsBean sessionSettingsBean, FieldConfigurationService fieldConfigurationService, FieldService fieldService, RedirectBean redirectBean) {
        super("Unité d'action", "bi bi-arrow-down-square", "siamois-panel action-unit-panel action-unit-single-panel");
        this.actionUnitService = actionUnitService;
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.fieldService = fieldService;
        this.redirectBean = redirectBean;

    }

    @Override
    public String display() {
        return "/panel/actionUnitPanel.xhtml";
    }

    public void init() {

            // reinit
            actionUnitErrorMessage = null;
            actionUnit = null;
            newCode = new ActionCode();
            secondaryActionCodes = new ArrayList<>();
            // Get the requested action from DB
            try {
                if (id != null) {
                    actionUnit = actionUnitService.findById(id);
                    secondaryActionCodes = new ArrayList<>(actionUnit.getSecondaryActionCodes());
                    fType = this.actionUnit.getType();
                    DefaultMenuItem item = DefaultMenuItem.builder()
                            .value(actionUnit.getName())
                            .icon("bi bi-arrow-down-square")
                            .build();
                    this.getBreadcrumb().getModel().getElements().add(item);
                } else {
                    log.error("The Action Unit page should not be accessed without ID or by direct page path");
                    redirectBean.redirectTo(HttpStatus.NOT_FOUND);
                }
            } catch (ActionUnitNotFoundException e) {
                log.error("Action unit with id {} not found", id);
                redirectBean.redirectTo(HttpStatus.NOT_FOUND);
            } catch (RuntimeException e) {
                this.actionUnitErrorMessage = "Failed to load action unit: " + e.getMessage();
                redirectBean.redirectTo(HttpStatus.INTERNAL_SERVER_ERROR);
            }




    }


    public String formatDate(OffsetDateTime offsetDateTime) {
        return DateUtils.formatOffsetDateTime(offsetDateTime);
    }

    public void saveNewActionCode() {
        // Update the action code
        if (newCodeIndex == 0) {
            // update primary action code
            actionUnit.setPrimaryActionCode(newCode);
        } else if (newCodeIndex > 0) {
            actionUnit.getSecondaryActionCodes().add(newCode);
            secondaryActionCodes.set(newCodeIndex - 1, newCode);
        }
    }






    /**
     * Fetch the autocomplete results for the action codes
     *
     * @param input the input of the user
     * @return the list of codes the input to display in the autocomplete
     */
    public List<ActionCode> completeActionCode(String input) {

        return actionUnitService.findAllActionCodeByCodeIsContainingIgnoreCase(input);

    }

    public String getUrlForActionCodeTypeFieldCode() {
        return fieldConfigurationService.getUrlForFieldCode(sessionSettingsBean.getUserInfo(), ActionCode.TYPE_FIELD_CODE);
    }

    /**
     * Fetch the autocomplete results on API for the action code type field
     *
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<Concept> completeActionCodeType(String input) {

        try {
            return fieldConfigurationService.fetchAutocomplete(sessionSettingsBean.getUserInfo(), ActionCode.TYPE_FIELD_CODE, input);
        } catch (NoConfigForFieldException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }

    }

    public void handleSelectPrimaryCode() {
        // To implement
    }

    public void addNewSecondaryCode() {
        ActionCode code = new ActionCode();
        Concept c = new Concept();
        code.setCode("");
        code.setType(c);
        secondaryActionCodes.add(code);
    }

    public void initNewActionCode(int index) {
        newCodeIndex = index;
        newCode = new ActionCode();
    }

    public void removeSecondaryCode(int index) {
        secondaryActionCodes.remove(index);
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

            return list.stream()
                    .map(item -> (item instanceof Concept concept) ? concept.getLabel() : item.toString())
                    .collect(Collectors.joining(", "));
        }

        return value.toString(); // Default case
    }



    public static class ActionUnitPanelBuilder {

        private final ActionUnitPanel actionUnitPanel;

        public ActionUnitPanelBuilder(ObjectProvider<ActionUnitPanel> actionUnitPanelProvider) {
            this.actionUnitPanel = actionUnitPanelProvider.getObject();
        }

        public ActionUnitPanelBuilder id(Long id) {
            actionUnitPanel.setId(id);
            return this;
        }

        public ActionUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            actionUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public ActionUnitPanel build() {
            actionUnitPanel.init();
            return actionUnitPanel;
        }
    }

}