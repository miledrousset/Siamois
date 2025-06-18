package fr.siamois.ui.bean.recordingunit;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectOneFromFieldCode;

import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectOneFromFieldCode;

import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerText;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;

import fr.siamois.domain.models.recordingunit.RecordingUnit;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.lazydatamodel.BaseRecordingUnitLazyDataModel;
import fr.siamois.ui.lazydatamodel.RecordingUnitInActionUnitLazyDataModel;
import fr.siamois.utils.MessageUtils;
import jakarta.el.MethodExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.event.AjaxBehaviorEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@Getter
@Setter
@SessionScoped
public class NewRecordingUnitBean implements Serializable {

    // Deps
    private final RecordingUnitService recordingUnitService;
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private final FlowBean flowBean;

    // Locals
    private RecordingUnit unit;
    private CustomFormPanel mainPanel ;
    private CustomFormResponse formResponse ;
    private CustomFieldSelectOneFromFieldCode typeField;
    private ActionUnit actionUnit ; // parent action unit for the new recording unit
    private SpatialUnit spatialUnit ; // parent spatial unit for the new recording unit
    private BaseRecordingUnitLazyDataModel lazyDataModel; // lazy data model to update after saving

    private static final String COLUMN_CLASS_NAME = "ui-g-12 ui-md-6 ui-lg-6";

    public NewRecordingUnitBean(RecordingUnitService recordingUnitService, LangBean langBean, SessionSettingsBean sessionSettingsBean, FlowBean flowBean) {
        this.recordingUnitService = recordingUnitService;
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.flowBean = flowBean;
    }

    public void setFieldConceptAnswerHasBeenModified(AjaxBehaviorEvent event) {
        UIComponent component = event.getComponent();
        CustomField field = (CustomField) component.getAttributes().get("field");

        formResponse.getAnswers().get(field).setHasBeenModified(true);
    }

    public void initForm() {

        mainPanel = new CustomFormPanel();
        mainPanel.setIsSystemPanel(true);
        mainPanel.setName("common.header.general");
        // two rows
        CustomRow row1 = new CustomRow();
        CustomRow row2 = new CustomRow();
        // First row: Type

        CustomCol col1 = new CustomCol();
        typeField = new CustomFieldSelectOneFromFieldCode();
        typeField.setLabel("spatialunit.field.type");
        typeField.setIsSystemField(true);
        typeField.setFieldCode(RecordingUnit.TYPE_FIELD_CODE);
        col1.setField(typeField);
        col1.setClassName(COLUMN_CLASS_NAME);

        row1.setColumns(List.of(col1));
        mainPanel.setRows(List.of(row1));


        // Init form answers
        formResponse = new CustomFormResponse();
        Map<CustomField, CustomFieldAnswer> answers = new HashMap<>();
        CustomFieldAnswerSelectOneFromFieldCode typeAnswer = new CustomFieldAnswerSelectOneFromFieldCode();
        typeAnswer.setHasBeenModified(false);
        answers.put(typeField, typeAnswer);
        formResponse.setAnswers(answers);

    }

    private void reset() {
        unit = null;
        actionUnit = null;
        formResponse = null;
        mainPanel = null;
        lazyDataModel = null;
    }

    public void init(RecordingUnitInActionUnitLazyDataModel lazyDataModel) {
        reset();
        unit = new RecordingUnit();
        actionUnit = lazyDataModel.getActionUnit();
        unit.setCreatedByInstitution(actionUnit.getCreatedByInstitution());
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        this.lazyDataModel = lazyDataModel;
        unit.setActionUnit(actionUnit);
        initForm();
    }

    public void create() {

        // Recupération des champs systeme
        // type
        CustomFieldAnswerSelectOneFromFieldCode typeAnswer = (CustomFieldAnswerSelectOneFromFieldCode) formResponse.getAnswers().get(typeField);
        unit.setType(typeAnswer.getValue());
        unit.setValidated(false);
        try {
            unit = recordingUnitService.save(unit, unit.getType(), List.of(), List.of(), List.of());
        }
        catch(FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", unit.getFullIdentifier());
            return ;
        }

        if(lazyDataModel != null) {
            lazyDataModel.addRowToModel(unit);
        }

        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getFullIdentifier());
    }

    public void createAndOpen() {

        // Recupération des champs systeme
        // type
        CustomFieldAnswerSelectOneFromFieldCode typeAnswer = (CustomFieldAnswerSelectOneFromFieldCode) formResponse.getAnswers().get(typeField);
        unit.setType(typeAnswer.getValue());
        unit.setValidated(false);
        try {
            unit = recordingUnitService.save(unit, unit.getType(), List.of(), List.of(), List.of());
        }
        catch(FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", unit.getFullIdentifier());
            return ;
        }

        if(lazyDataModel != null) {
            lazyDataModel.addRowToModel(unit);
        }

        // Open new panel
        flowBean.addRecordingUnitPanel(unit.getId());

        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getFullIdentifier());
    }
}
