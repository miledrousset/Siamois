package fr.siamois.ui.bean.panel.models.panel.single;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.form.customfield.*;
import fr.siamois.domain.models.form.customfieldanswer.*;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.spatialunit.SpatialUnitTreeService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import fr.siamois.utils.DateUtils;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Supplier;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@Slf4j
public abstract class AbstractSingleEntity<T> extends AbstractPanel implements Serializable {

    private static SpatialUnit spatialUnit;
    // Deps
    protected final transient SessionSettingsBean sessionSettingsBean;
    protected final transient FieldConfigurationService fieldConfigurationService;
    private final transient SpatialUnitTreeService spatialUnitTreeService;

    //--------------- Locals
    protected transient T unit;
    protected CustomFormResponse formResponse; // answers to all the fields from overview and details
    protected Boolean hasUnsavedModifications; // Did we modify the unit?

    //
    protected CustomForm detailsForm;

    public static String generateRandomActionUnitIdentifier() {
        return "2025"; // todo : real implementation
    }

    public static final Vocabulary SYSTEM_THESO;

    static {
        SYSTEM_THESO = new Vocabulary();
        SYSTEM_THESO.setBaseUri("https://thesaurus.mom.fr/");
        SYSTEM_THESO.setExternalVocabularyId("th230");
    }


    private static final Map<Class<? extends CustomField>, Supplier<? extends CustomFieldAnswer>> ANSWER_CREATORS = Map.of(
            CustomFieldText.class, CustomFieldAnswerText::new,
            CustomFieldSelectOneFromFieldCode.class, CustomFieldAnswerSelectOneFromFieldCode::new,
            CustomFieldSelectOneConceptFromChildrenOfConcept.class, CustomFieldAnswerSelectOneConceptFromChildrenOfConcept::new,
            CustomFieldSelectMultiplePerson.class, CustomFieldAnswerSelectMultiplePerson::new,
            CustomFieldDateTime.class, CustomFieldAnswerDateTime::new,
            CustomFieldSelectOneActionUnit.class, CustomFieldAnswerSelectOneActionUnit::new,
            CustomFieldSelectOneSpatialUnit.class, CustomFieldAnswerSelectOneSpatialUnit::new,
            CustomFieldSelectMultipleSpatialUnitTree.class, CustomFieldAnswerSelectMultipleSpatialUnitTree::new
    );

    public boolean hasAutoGenerationFunction(CustomFieldText field) {
        return field != null && field.getAutoGenerationFunction() != null;
    }

    public void generateValueForField(CustomFieldText field, CustomFieldAnswerText answer) {
        if (field != null && field.getAutoGenerationFunction() != null) {
            String generatedValue = field.generateAutoValue();
            answer.setValue(generatedValue);
            setFieldAnswerHasBeenModified(field);
        }
    }

    public static final String COLUMN_CLASS_NAME = "ui-g-12 ui-md-6 ui-lg-4";

    protected AbstractSingleEntity() {
        super();
        this.sessionSettingsBean = null;
        this.fieldConfigurationService = null;
        this.spatialUnitTreeService = null;

    }

    protected AbstractSingleEntity(SessionSettingsBean sessionSettingsBean,
                                   FieldConfigurationService fieldConfigurationService,
                                   SpatialUnitTreeService spatialUnitTreeService) {
        super();
        this.sessionSettingsBean = sessionSettingsBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.spatialUnitTreeService = spatialUnitTreeService;
    }

    protected AbstractSingleEntity(String titleCodeOrTitle,
                                   String icon,
                                   String panelClass,
                                   SessionSettingsBean sessionSettingsBean,
                                   FieldConfigurationService fieldConfigurationService,
                                   SpatialUnitTreeService spatialUnitTreeService) {
        super(titleCodeOrTitle, icon, panelClass);
        this.sessionSettingsBean = sessionSettingsBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.spatialUnitTreeService = spatialUnitTreeService;
    }

    public String formatDate(OffsetDateTime offsetDateTime) {
        return DateUtils.formatOffsetDateTime(offsetDateTime);
    }


    public String getAutocompleteClass() {
        // Default implementation
        return "";
    }


    public abstract void initForms();


    public List<SpatialUnit> getSpatialUnitOptions() {
        // Implement in child classes if necessary
        return List.of();
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


    public List<Concept> completeDependentConceptChildren(
            String input
    ) {

        FacesContext context = FacesContext.getCurrentInstance();
        CustomFieldSelectOneConceptFromChildrenOfConcept dependentField =
                (CustomFieldSelectOneConceptFromChildrenOfConcept) UIComponent.getCurrentComponent(context).getAttributes().get("field");

        CustomField parentField = dependentField.getParentField();
        if (parentField == null) {
            return Collections.emptyList();
        }

        CustomFieldAnswer answer = formResponse.getAnswers().get(parentField);
        Concept parentConcept = null;

        if (answer instanceof CustomFieldAnswerSelectOneConceptFromChildrenOfConcept a1) {
            parentConcept = a1.getValue();
        } else if (answer instanceof CustomFieldAnswerSelectOneFromFieldCode a2) {
            parentConcept = a2.getValue();
        }

        if (parentConcept == null) {
            return Collections.emptyList();
        }

        UserInfo userInfo = sessionSettingsBean.getUserInfo();
        return fieldConfigurationService.fetchAutocomplete(userInfo, parentConcept, input);
    }

    public String getUrlForDependentConcept(
            CustomFieldSelectOneConceptFromChildrenOfConcept dependentField
    ) {
        if (dependentField == null || dependentField.getParentField() == null) {
            return null;
        }

        CustomField parentField = dependentField.getParentField();
        CustomFieldAnswer parentAnswer = formResponse.getAnswers().get(parentField);

        Concept parentConcept = null;

        if (parentAnswer instanceof CustomFieldAnswerSelectOneConceptFromChildrenOfConcept a1) {
            parentConcept = a1.getValue();
        } else if (parentAnswer instanceof CustomFieldAnswerSelectOneFromFieldCode a2) {
            parentConcept = a2.getValue();
        }

        return parentConcept != null ? fieldConfigurationService.getUrlOfConcept(parentConcept) : null;
    }

    public CustomFormResponse initializeFormResponse(CustomForm form, Object jpaEntity) {
        CustomFormResponse response = new CustomFormResponse();
        if (form.getLayout() == null) return response;

        Map<CustomField, CustomFieldAnswer> answers = new HashMap<>();
        List<String> bindableFields = getBindableFieldNames(jpaEntity);

        for (CustomFormPanel panel : form.getLayout()) {
            processPanel(panel, jpaEntity, bindableFields, answers);
        }

        response.setAnswers(answers);
        return response;
    }

    private void processPanel(CustomFormPanel panel, Object jpaEntity, List<String> bindableFields, Map<CustomField, CustomFieldAnswer> answers) {
        if (panel.getRows() == null) return;

        for (CustomRow row : panel.getRows()) {
            if (row.getColumns() == null) continue;

            for (CustomCol col : row.getColumns()) {
                processColumn(col, jpaEntity, bindableFields, answers);
            }
        }
    }

    private void processColumn(CustomCol col, Object jpaEntity, List<String> bindableFields, Map<CustomField, CustomFieldAnswer> answers) {
        CustomField field = col.getField();
        if (field == null || answers.containsKey(field)) return;

        CustomFieldAnswer answer = instantiateAnswerForField(field);
        if (answer == null) return;

        initializeAnswer(answer, field);

        if (Boolean.TRUE.equals(field.getIsSystemField())
                && field.getValueBinding() != null
                && bindableFields.contains(field.getValueBinding())) {

            populateSystemFieldValue(answer, jpaEntity, field);
        }

        answers.put(field, answer);
    }

    private void markSelected(TreeNode<SpatialUnit> node, Set<SpatialUnit> selectedUnits) {
        if (node == null) return;

        SpatialUnit data = node.getData();
        if (data != null && selectedUnits.contains(data)) {
            node.setSelected(true);
        }

        for (TreeNode<SpatialUnit> child : node.getChildren()) {
            markSelected(child, selectedUnits);
        }
    }

    private static void initializeAnswer(CustomFieldAnswer answer, CustomField field) {
        CustomFieldAnswerId answerId = new CustomFieldAnswerId();
        answerId.setField(field);
        answer.setPk(answerId);
        answer.setHasBeenModified(false);
    }

    private List<CheckboxTreeNode<SpatialUnit>> getSelectedNodes(TreeNode<SpatialUnit> root) {
        List<CheckboxTreeNode<SpatialUnit>> selectedNodes = new ArrayList<>();
        collectSelectedNodes(root, selectedNodes);
        return selectedNodes;
    }

    private void collectSelectedNodes(TreeNode<SpatialUnit> node, List<CheckboxTreeNode<SpatialUnit>> selectedNodes) {
        if (node instanceof CheckboxTreeNode<SpatialUnit> cbNode && cbNode.isSelected()) {
            selectedNodes.add(cbNode);
        }

        for (TreeNode<SpatialUnit> child : node.getChildren()) {
            collectSelectedNodes(child, selectedNodes);
        }
    }

    private void populateSystemFieldValue(CustomFieldAnswer answer, Object jpaEntity, CustomField field) {
        Object value = getFieldValue(jpaEntity, field.getValueBinding()); // todo ; reduce cognitive complexity

        if (value instanceof OffsetDateTime odt && answer instanceof CustomFieldAnswerDateTime dateTimeAnswer) {
            dateTimeAnswer.setValue(odt.toLocalDateTime());
        } else if (value instanceof String str && answer instanceof CustomFieldAnswerText textAnswer) {
            textAnswer.setValue(str);
        } else if (value instanceof List<?> list && answer instanceof CustomFieldAnswerSelectMultiplePerson multiplePersonAnswer &&
                list.stream().allMatch(Person.class::isInstance)) {
            multiplePersonAnswer.setValue((List<Person>) list);
        } else if (value instanceof Concept c) {
            if (answer instanceof CustomFieldAnswerSelectOneFromFieldCode codeAnswer) {
                codeAnswer.setValue(c);
            } else if (answer instanceof CustomFieldAnswerSelectOneConceptFromChildrenOfConcept childAnswer) {
                childAnswer.setValue(c);
            }
        } else if (value instanceof ActionUnit a && answer instanceof CustomFieldAnswerSelectOneActionUnit actionUnitAnswer) {
            actionUnitAnswer.setValue(a);
        } else if (value instanceof SpatialUnit s && answer instanceof CustomFieldAnswerSelectOneSpatialUnit spatialUnitAnswer) {
            spatialUnitAnswer.setValue(s);
        } else if (value instanceof Set<?> set && answer instanceof CustomFieldAnswerSelectMultipleSpatialUnitTree treeAnswer) {
            // Cast set to the expected type
            Set<SpatialUnit> selectedSpatialUnits = (Set<SpatialUnit>) set;

            // Build the root tree
            TreeNode<SpatialUnit> root = spatialUnitTreeService.buildTree();

            // Mark selected nodes based on spatial units
            markSelected(root, selectedSpatialUnits);

            // Set root and selected nodes into the answer
            treeAnswer.setRoot(root);
            treeAnswer.setValue(getSelectedNodes(root));
        }
    }


    public static void updateJpaEntityFromFormResponse(CustomFormResponse response, Object jpaEntity) {
        if (response == null || jpaEntity == null) return;

        List<String> bindableFields = getBindableFieldNames(jpaEntity);

        for (Map.Entry<CustomField, CustomFieldAnswer> entry : response.getAnswers().entrySet()) {
            CustomField field = entry.getKey();
            CustomFieldAnswer answer = entry.getValue();

            if (!isBindableSystemField(field, answer, bindableFields)) continue;

            Object value = extractValueFromAnswer(answer);
            if (value != null) {
                setFieldValue(jpaEntity, field.getValueBinding(), value);
            }
        }
    }

    private static boolean isBindableSystemField(CustomField field, CustomFieldAnswer answer, List<String> bindableFields) {
        return field != null
                && answer != null
                && Boolean.TRUE.equals(field.getIsSystemField())
                && field.getValueBinding() != null
                && bindableFields.contains(field.getValueBinding());
    }

    private static Object extractValueFromAnswer(CustomFieldAnswer answer) {
        if (answer instanceof CustomFieldAnswerDateTime a && a.getValue() != null) {
            return a.getValue().atOffset(ZoneOffset.UTC);
        } else if (answer instanceof CustomFieldAnswerText a) {
            return a.getValue();
        } else if (answer instanceof CustomFieldAnswerSelectMultiplePerson a) {
            return a.getValue();
        } else if (answer instanceof CustomFieldAnswerSelectOneFromFieldCode a) {
            return a.getValue();
        } else if (answer instanceof CustomFieldAnswerSelectOneConceptFromChildrenOfConcept a) {
            return a.getValue();
        } else if (answer instanceof CustomFieldAnswerSelectOneActionUnit a) {
            return a.getValue();
        } else if (answer instanceof CustomFieldAnswerSelectOneSpatialUnit a) {
            return a.getValue();
        } else if (answer instanceof CustomFieldAnswerSelectMultipleSpatialUnitTree a) {
            Set<SpatialUnit> ret = new HashSet<>();
            for (CheckboxTreeNode<SpatialUnit> su : a.getValue()) {
                SpatialUnit spatialUnit = su.getData();
                ret.add(spatialUnit);
            }
            return ret;
        }


        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<String> getBindableFieldNames(Object entity) {
        try {
            Method method = entity.getClass().getMethod("getBindableFieldNames");
            return (List<String>) method.invoke(entity);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
            return pd.getReadMethod().invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
            Method setter = pd.getWriteMethod();
            setter.invoke(obj, value);
        } catch (Exception e) {
            // Ignored, the value won't be set
        }
    }

    private static CustomFieldAnswer instantiateAnswerForField(CustomField field) {
        Supplier<? extends CustomFieldAnswer> creator = ANSWER_CREATORS.get(field.getClass());
        if (creator != null) {
            return creator.get();
        }
        throw new IllegalArgumentException("Unsupported CustomField type: " + field.getClass().getName());
    }




}
