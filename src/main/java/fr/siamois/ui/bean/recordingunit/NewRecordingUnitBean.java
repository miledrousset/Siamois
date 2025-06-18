package fr.siamois.ui.bean.recordingunit;

import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectOneFromFieldCode;

import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectOneFromFieldCode;

import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;

import fr.siamois.domain.models.recordingunit.RecordingUnit;

import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import jakarta.faces.component.UIComponent;
import jakarta.faces.event.AjaxBehaviorEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@Getter
@Setter
@ViewScoped
public class NewRecordingUnitBean implements Serializable {

    // Deps
    private final RecordingUnitService recordingUnitService;

    // Locals
    public RecordingUnit recordingUnit;
    public CustomFormPanel mainPanel ;
    public CustomFormResponse formResponse ;
    private CustomFieldSelectOneFromFieldCode typeField;
    public boolean hasUnsavedModifications;
    public static final String COLUMN_CLASS_NAME = "ui-g-12 ui-md-6 ui-lg-6";

    public NewRecordingUnitBean(RecordingUnitService recordingUnitService) {
        this.recordingUnitService = recordingUnitService;
    }

    public void setFieldConceptAnswerHasBeenModified(AjaxBehaviorEvent event) {
        UIComponent component = event.getComponent();
        CustomField field = (CustomField) component.getAttributes().get("field");

        formResponse.getAnswers().get(field).setHasBeenModified(true);
        hasUnsavedModifications = true;
    }

    public void initForm() {

        CustomFormPanel mainPanel = new CustomFormPanel();
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


    public void init() {
        recordingUnit = new RecordingUnit();
        initForm();
    }

}
