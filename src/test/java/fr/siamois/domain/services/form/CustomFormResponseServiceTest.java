package fr.siamois.domain.services.form;

import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldInteger;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectMultiple;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerId;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerInteger;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectMultiple;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.vocabulary.Concept;

import fr.siamois.infrastructure.repositories.form.CustomFormRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomFormResponseServiceTest {

    @Mock
    private CustomFormRepository customFormRepository;

    @InjectMocks
    private CustomFormResponseService customFormResponseService;

    // Locals
    CustomFormResponse customFormResponse; // The new form response to be submitted
    CustomFormResponse managedFormResponse; // the managed instance retrieved from DB and to be modified based on submision
    Map<CustomField, CustomFieldAnswer> toBeDeleted; // Field answers to be deleted
    CustomField managedField; // The field to process
    CustomForm form; // A form
    CustomFieldSelectMultiple field1SelectMultiple;
    CustomFieldSelectMultiple field2SelectMultiple;
    CustomFieldSelectMultiple field3SelectMultiple;
    CustomFieldInteger field1Integer;
    CustomFieldInteger field2Integer;
    CustomFieldInteger field3Integer;
    Concept field1Concept;
    Concept field2Concept;
    Concept field3Concept;
    Concept field4Concept;
    Concept field5Concept;
    Concept field6Concept;
    Concept answer1Value ;
    Concept answer2ValueOld ;
    Concept answer2ValueNew;
    Concept answer3Value;

    @BeforeEach
    void setUp() {
        // Concepts
        field1Concept = new Concept();
        field1Concept.setExternalId("q1");
        field2Concept = new Concept();
        field2Concept.setExternalId("q2");
        field3Concept = new Concept();
        field3Concept.setExternalId("q3");
        field4Concept = new Concept();
        field4Concept.setExternalId("q4");
        field5Concept = new Concept();
        field5Concept.setExternalId("q5");
        field6Concept = new Concept();
        field6Concept.setExternalId("q6");
        answer1Value = new Concept();
        answer2ValueOld = new Concept();
        answer2ValueNew = new Concept();
        answer3Value = new Concept();
        answer1Value.setExternalId("a1");
        answer2ValueOld.setExternalId("a2_old");
        answer2ValueNew.setExternalId("a2_new");
        answer3Value.setExternalId("a3");
        // The fields we are going to test
        field1SelectMultiple = new CustomFieldSelectMultiple();
        field1SelectMultiple.setLabel("Select Multiple");
        field1SelectMultiple.setConcept(field1Concept);
        field2SelectMultiple = new CustomFieldSelectMultiple();
        field2SelectMultiple.setLabel("Select Multiple");
        field2SelectMultiple.setConcept(field2Concept);
        field3SelectMultiple = new CustomFieldSelectMultiple();
        field3SelectMultiple.setLabel("Select Multiple");
        field3SelectMultiple.setConcept(field3Concept);
        field1Integer = new CustomFieldInteger();
        field1Integer.setLabel("Integer");
        field1Integer.setConcept(field4Concept);
        field2Integer = new CustomFieldInteger();
        field2Integer.setLabel("Integer");
        field2Integer.setConcept(field5Concept);
        field3Integer = new CustomFieldInteger();
        field3Integer.setLabel("Integer");
        field3Integer.setConcept(field6Concept);

        // ------------ Define the form
        CustomFormPanel customFormPanel = new CustomFormPanel();
        customFormPanel.setFields(new ArrayList<>());
        customFormPanel.getFields().add(field1Integer);
        customFormPanel.getFields().add(field2Integer);
        customFormPanel.getFields().add(field3Integer);
        customFormPanel.getFields().add(field1SelectMultiple);
        customFormPanel.getFields().add(field2SelectMultiple);
        customFormPanel.getFields().add(field3SelectMultiple);
        form = new CustomForm();
        form.setLayout(new ArrayList<>());
        form.getLayout().add(customFormPanel);

        // Prepare the answers
        // Two answers won't be modified bc they did not change
        CustomFieldAnswerSelectMultiple a1 = new CustomFieldAnswerSelectMultiple();
        CustomFieldAnswerId pk1 = new CustomFieldAnswerId();
        pk1.setField(field1SelectMultiple);
        a1.setPk(pk1);
        a1.setValue(new ArrayList<>());
        a1.getValue().add(answer1Value);
        CustomFieldAnswerInteger a2 = new CustomFieldAnswerInteger();
        CustomFieldAnswerId pk2 = new CustomFieldAnswerId();
        pk2.setField(field1Integer);
        a2.setPk(pk2);
        a2.setValue(2);
        // Two answers will be updated because the value did change
        CustomFieldAnswerSelectMultiple a3Old = new CustomFieldAnswerSelectMultiple();
        CustomFieldAnswerId pk3 = new CustomFieldAnswerId();
        pk3.setField(field2SelectMultiple);
        a3Old.setPk(pk3);
        a3Old.setValue(new ArrayList<>());
        a3Old.getValue().add(answer2ValueOld);
        CustomFieldAnswerSelectMultiple a3new = new CustomFieldAnswerSelectMultiple();
        pk3.setField(field2SelectMultiple);
        a3new.setPk(pk3);
        a3new.setValue(new ArrayList<>());
        a3new.getValue().add(answer2ValueNew);
        CustomFieldAnswerInteger a4old = new CustomFieldAnswerInteger();
        CustomFieldAnswerId pk4 = new CustomFieldAnswerId();
        pk4.setField(field2Integer);
        a4old.setPk(pk4);
        a4old.setValue(3);
        CustomFieldAnswerInteger a4new = new CustomFieldAnswerInteger();
        pk4.setField(field2Integer);
        a4new.setPk(pk4);
        a4new.setValue(4);
        // Two answers will be created
        CustomFieldAnswerSelectMultiple a5 = new CustomFieldAnswerSelectMultiple();
        CustomFieldAnswerId pk5 = new CustomFieldAnswerId();
        pk5.setField(field3SelectMultiple);
        a5.setPk(pk5);
        a5.setValue(new ArrayList<>());
        a5.getValue().add(answer3Value);
        CustomFieldAnswerInteger a6 = new CustomFieldAnswerInteger();
        CustomFieldAnswerId pk6 = new CustomFieldAnswerId();
        pk6.setField(field1Integer);
        a6.setPk(pk6);
        a6.setValue(6);


        // Prepare the form response to be modified
        managedFormResponse = new CustomFormResponse();
        managedFormResponse.setForm(form);
        managedFormResponse.setAnswers(new HashMap<>());
        managedFormResponse.getAnswers().put(field1SelectMultiple,a1);
        managedFormResponse.getAnswers().put(field1Integer,a2);
        managedFormResponse.getAnswers().put(field2SelectMultiple,a3Old);
        managedFormResponse.getAnswers().put(field2Integer,a4old);
        // -------------

        // Prepare the form response to be submitted
        customFormResponse = new CustomFormResponse();
        customFormResponse.setForm(form);
        customFormResponse.setAnswers(new HashMap<>());
        customFormResponse.getAnswers().put(field1SelectMultiple,a1);
        customFormResponse.getAnswers().put(field1Integer,a2);
        customFormResponse.getAnswers().put(field2SelectMultiple,a3new);
        customFormResponse.getAnswers().put(field2Integer,a4new);
        customFormResponse.getAnswers().put(field3SelectMultiple,a5);
        customFormResponse.getAnswers().put(field3Integer,a6);
        // -------------

        // Init the list with the answers to be deleted
        toBeDeleted = new HashMap<>(managedFormResponse.getAnswers());

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void saveAnswer_UnchangedSelectMultiple() {

        // Initialize to be deleted with the old values from the form response
        customFormResponseService.saveAnswer(
                field1SelectMultiple, customFormResponse, managedFormResponse, toBeDeleted
        );

        CustomFieldAnswerSelectMultiple answer = (CustomFieldAnswerSelectMultiple) managedFormResponse.getAnswers().get(field1SelectMultiple);

        // Assess that the answer in the managedformresponse is unchanged
        assertEquals( 1,answer.getValue().size());
        // Assess that value is unchanged
        assertTrue(answer.getValue().contains(answer1Value));
        // Assess that the field has be removed from tobedeleted
        assertFalse(toBeDeleted.containsKey(field1SelectMultiple));


    }

    @Test
    void saveAnswer_UpdatedSelectMultiple() {

        // Initialize to be deleted with the old values from the form response
        customFormResponseService.saveAnswer(
                field2SelectMultiple, customFormResponse, managedFormResponse, toBeDeleted
        );

        CustomFieldAnswerSelectMultiple answer = (CustomFieldAnswerSelectMultiple)
                managedFormResponse.getAnswers().get(field2SelectMultiple);


        assertEquals( 1,answer.getValue().size());
        // Assess that value is unchanged
        assertTrue(answer.getValue().contains(answer2ValueNew));
        // Assess that the field has be removed from tobedeleted
        assertFalse(toBeDeleted.containsKey(field2SelectMultiple));


    }

    @Test
    void saveAnswer_AddedSelectMultiple() {

        // Initialize to be deleted with the old values from the form response
        customFormResponseService.saveAnswer(
                field3SelectMultiple, customFormResponse, managedFormResponse, toBeDeleted
        );

        CustomFieldAnswerSelectMultiple answer = (CustomFieldAnswerSelectMultiple)
                managedFormResponse.getAnswers().get(field3SelectMultiple);


        assertEquals( 1,answer.getValue().size());
        // Assess that value is unchanged
        assertTrue(answer.getValue().contains(answer3Value));



    }

    @Test
    void saveAnswer_UnchangedInteger() {

        // Initialize to be deleted with the old values from the form response
        customFormResponseService.saveAnswer(
                field1Integer, customFormResponse, managedFormResponse, toBeDeleted
        );

            CustomFieldAnswerInteger answer =
                    (CustomFieldAnswerInteger) managedFormResponse
                            .getAnswers().get(field1Integer);

        // Assess that the value is unchanged
        assertEquals( 2,answer.getValue());

        // Assess that the field has be removed from tobedeleted
        assertFalse(toBeDeleted.containsKey(field1Integer));


    }

    @Test
    void saveAnswer_UpdatedInteger() {

        // Initialize to be deleted with the old values from the form response
        customFormResponseService.saveAnswer(
                field2Integer, customFormResponse, managedFormResponse, toBeDeleted
        );

        CustomFieldAnswerInteger answer =
                (CustomFieldAnswerInteger) managedFormResponse
                        .getAnswers().get(field2Integer);

        // Assess that the value is unchanged
        assertEquals( 4,answer.getValue());

        // Assess that the field has be removed from tobedeleted
        assertFalse(toBeDeleted.containsKey(field2Integer));


    }

    @Test
    void saveAnswer_AddedInteger() {

        // Initialize to be deleted with the old values from the form response
        customFormResponseService.saveAnswer(
                field3Integer, customFormResponse, managedFormResponse, toBeDeleted
        );

        CustomFieldAnswerInteger answer =
                (CustomFieldAnswerInteger) managedFormResponse
                        .getAnswers().get(field3Integer);

        // Assess that the value is unchanged
        assertEquals( 6,answer.getValue());



    }

    @Test
    void saveFormResponse_success() {
        //mock

        when(customFormRepository.findById(anyLong()))
                .thenReturn(Optional.of(form));

        doNothing().when(customFormResponseService).saveAnswer(
                any(CustomField.class),
                any(CustomFormResponse.class),
                any(CustomFormResponse.class),
                any()
        );

        customFormResponseService.saveFormResponse(
                managedFormResponse,
                customFormResponse
        );
    }
}