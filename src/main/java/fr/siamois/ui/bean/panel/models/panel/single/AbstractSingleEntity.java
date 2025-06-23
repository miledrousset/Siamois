package fr.siamois.ui.bean.panel.models.panel.single;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
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
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.document.DocumentCreationBean;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.utils.DateUtils;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class AbstractSingleEntity<T> extends AbstractPanel {

    // Deps
    protected final transient SessionSettingsBean sessionSettingsBean;
    protected final transient FieldConfigurationService fieldConfigurationService;

    //--------------- Locals
    protected transient T unit;
    protected CustomFormResponse formResponse; // answers to all the fields from overview and details
    protected Boolean hasUnsavedModifications; // Did we modify the unit?

    //
    protected CustomForm detailsForm;

    public static final Vocabulary SYSTEM_THESO;

    static {
        SYSTEM_THESO = new Vocabulary();
        SYSTEM_THESO.setBaseUri("https://thesaurus.mom.fr/");
        SYSTEM_THESO.setExternalVocabularyId("th230");
    }

    protected static final String COLUMN_CLASS_NAME = "ui-g-12 ui-md-6 ui-lg-4";

    protected AbstractSingleEntity() {
        super();
        this.sessionSettingsBean = null;
        this.fieldConfigurationService = null;

    }

    protected AbstractSingleEntity(SessionSettingsBean sessionSettingsBean, FieldConfigurationService fieldConfigurationService) {
        super();
        this.sessionSettingsBean = sessionSettingsBean;
        this.fieldConfigurationService = fieldConfigurationService;
    }

    protected AbstractSingleEntity(String titleCodeOrTitle, String icon, String panelClass,
                                   SessionSettingsBean sessionSettingsBean, FieldConfigurationService fieldConfigurationService) {
        super(titleCodeOrTitle, icon, panelClass);
        this.sessionSettingsBean = sessionSettingsBean;
        this.fieldConfigurationService = fieldConfigurationService;
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
        return fieldConfigurationService.fetchConceptChildrenAutocomplete(userInfo, parentConcept, input);
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

    public static CustomFormResponse initializeFormResponse(CustomForm form, Object jpaEntity) {
        CustomFormResponse response = new CustomFormResponse();
        Map<CustomField, CustomFieldAnswer> answers = new HashMap<>();

        List<String> bindableFields = getBindableFieldNames(jpaEntity);

        if (form.getLayout() == null) return response;

        for (CustomFormPanel panel : form.getLayout()) {
            if (panel.getRows() == null) continue;

            for (CustomRow row : panel.getRows()) {
                if (row.getColumns() == null) continue;

                for (CustomCol col : row.getColumns()) {
                    CustomField field = col.getField();
                    if (field != null && !answers.containsKey(field)) {
                        CustomFieldAnswer answer = instantiateAnswerForField(field);
                        if (answer != null) {
                            CustomFieldAnswerId answerId = new CustomFieldAnswerId();
                            answerId.setField(field);
                            answer.setPk(answerId);
                            answer.setHasBeenModified(false);

                            if (Boolean.TRUE.equals(field.getIsSystemField())
                                    && field.getValueBinding() != null
                                    && bindableFields.contains(field.getValueBinding())) {

                                Object value = getFieldValue(jpaEntity, field.getValueBinding());
                                if (value instanceof OffsetDateTime odt && answer instanceof CustomFieldAnswerDateTime) {
                                    ((CustomFieldAnswerDateTime) answer).setValue(odt.toLocalDateTime());
                                } else if (value instanceof String odt && answer instanceof CustomFieldAnswerText) {
                                    ((CustomFieldAnswerText) answer).setValue(odt);
                                } else if (value instanceof List<?> list && answer instanceof CustomFieldAnswerSelectMultiplePerson &&
                                        list.stream().allMatch(p -> p instanceof Person)) {
                                    ((CustomFieldAnswerSelectMultiplePerson) answer).setValue((List<Person>) list);
                                } else if (value instanceof Concept c && answer instanceof CustomFieldAnswerSelectOneFromFieldCode) {
                                    ((CustomFieldAnswerSelectOneFromFieldCode) answer).setValue(c);
                                }else if (value instanceof Concept c && answer instanceof CustomFieldAnswerSelectOneConceptFromChildrenOfConcept) {
                                    ((CustomFieldAnswerSelectOneConceptFromChildrenOfConcept) answer).setValue(c);
                                }else if (value instanceof ActionUnit a && answer instanceof CustomFieldAnswerSelectOneActionUnit) {
                                    ((CustomFieldAnswerSelectOneActionUnit) answer).setValue(a);
                                }else if (value instanceof SpatialUnit a && answer instanceof CustomFieldAnswerSelectOneSpatialUnit) {
                                    ((CustomFieldAnswerSelectOneSpatialUnit) answer).setValue(a);
                                }
                            }

                            answers.put(field, answer);
                        }
                    }
                }
            }
        }

        response.setAnswers(answers);
        return response;
    }

    public static void updateJpaEntityFromFormResponse(CustomFormResponse response, Object jpaEntity) {
        if (response == null || jpaEntity == null) return;

        List<String> bindableFields = getBindableFieldNames(jpaEntity);

        for (Map.Entry<CustomField, CustomFieldAnswer> entry : response.getAnswers().entrySet()) {
            CustomField field = entry.getKey();
            CustomFieldAnswer answer = entry.getValue();

            if (field == null || answer == null) continue;

            String binding = field.getValueBinding();
            if (!Boolean.TRUE.equals(field.getIsSystemField()) || binding == null || !bindableFields.contains(binding)) {
                continue;
            }

            Object value = null;

            if (answer instanceof CustomFieldAnswerDateTime a && a.getValue() != null) {
                value = a.getValue().atOffset(ZoneOffset.UTC);
            } else if (answer instanceof CustomFieldAnswerText a) {
                value = a.getValue();
            } else if (answer instanceof CustomFieldAnswerSelectMultiplePerson a) {
                value = a.getValue();
            } else if (answer instanceof CustomFieldAnswerSelectOneFromFieldCode a) {
                value = a.getValue();
            } else if (answer instanceof CustomFieldAnswerSelectOneConceptFromChildrenOfConcept a) {
                value = a.getValue();
            }else if (answer instanceof CustomFieldAnswerSelectOneActionUnit a) {
                value = a.getValue();
            }else if (answer instanceof CustomFieldAnswerSelectOneSpatialUnit a) {
                value = a.getValue();
            }

            if (value != null) {
                setFieldValue(jpaEntity, binding, value);
            }
        }
    }


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
            Field field = findField(obj.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + fieldName + " not found in " + clazz);
    }

    private static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = findField(obj.getClass(), fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {

        }
    }

    private static CustomFieldAnswer instantiateAnswerForField(CustomField field) {
        if (field instanceof CustomFieldText) {
            return new CustomFieldAnswerText();
        } else if (field instanceof CustomFieldSelectOneFromFieldCode) {
            return new CustomFieldAnswerSelectOneFromFieldCode();
        } else if (field instanceof CustomFieldSelectOneConceptFromChildrenOfConcept) {
            return new CustomFieldAnswerSelectOneConceptFromChildrenOfConcept();
        } else if (field instanceof CustomFieldSelectMultiplePerson) {
            return new CustomFieldAnswerSelectMultiplePerson();
        } else if (field instanceof CustomFieldDateTime) {
            return new CustomFieldAnswerDateTime();
        } else if (field instanceof CustomFieldSelectOneActionUnit) {
            return new CustomFieldAnswerSelectOneActionUnit();
        }else if (field instanceof CustomFieldSelectOneSpatialUnit) {
            return new CustomFieldAnswerSelectOneSpatialUnit();
        }

        return null;
    }


}
