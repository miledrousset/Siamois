package fr.siamois.domain.services.form;

import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldInteger;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectMultiple;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerId;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerInteger;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectMultiple;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.repositories.FieldRepository;
import fr.siamois.infrastructure.repositories.form.CustomFieldAnswerRepository;
import fr.siamois.infrastructure.repositories.form.CustomFieldRepository;
import fr.siamois.infrastructure.repositories.form.CustomFormRepository;
import fr.siamois.infrastructure.repositories.form.CustomFormResponseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomFormResponseService {
    private final CustomFormResponseRepository customFormResponseRepository;
    private final CustomFieldAnswerRepository customFieldAnswerRepository;
    private final CustomFieldRepository customFieldRepository;
    private final CustomFormRepository customFormRepository;
    private final FieldRepository fieldRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public CustomFormResponseService(CustomFormResponseRepository customFormResponseRepository, CustomFieldAnswerRepository customFieldAnswerRepository, CustomFieldRepository customFieldRepository, CustomFormRepository customFormRepository, FieldRepository fieldRepository) {
        this.customFormResponseRepository = customFormResponseRepository;
        this.customFieldAnswerRepository = customFieldAnswerRepository;
        this.customFieldRepository = customFieldRepository;
        this.customFormRepository = customFormRepository;
        this.fieldRepository = fieldRepository;
    }

    private CustomFieldAnswer createAnswer(CustomFieldAnswerId pk, CustomFieldAnswer answer) {
        CustomFieldAnswer createdAnswer;
        if (pk.getField() instanceof CustomFieldInteger) {
            createdAnswer = new CustomFieldAnswerInteger();
            ((CustomFieldAnswerInteger) createdAnswer).setValue(((CustomFieldAnswerInteger) answer).getValue());
        } else if (pk.getField() instanceof CustomFieldSelectMultiple) {
            createdAnswer = new CustomFieldAnswerSelectMultiple();
            ((CustomFieldAnswerSelectMultiple) createdAnswer).setValue(((CustomFieldAnswerSelectMultiple) answer).getValue());
        } else {
            throw new IllegalArgumentException("Unsupported field type");
        }
        createdAnswer.setPk(pk);
        return createdAnswer;
    }

    private boolean hasValueChanged(CustomField field, CustomFieldAnswer existingAnswer, CustomFieldAnswer newValue) {
        if (field instanceof CustomFieldInteger) {
            return !Objects.equals(((CustomFieldAnswerInteger) existingAnswer).getValue(),
                    ((CustomFieldAnswerInteger) newValue).getValue());
        } else if (field instanceof CustomFieldSelectMultiple) {
            // Checking if the answer contains the same  IDs
            return !Objects.equals(
                    ((CustomFieldAnswerSelectMultiple) existingAnswer).getValue().stream()
                            .map(Concept::getId)
                            .collect(Collectors.toSet()),

                    ((CustomFieldAnswerSelectMultiple) newValue).getValue().stream()
                            .map(Concept::getId)
                            .collect(Collectors.toSet())
            );
        } else {
            throw new IllegalArgumentException("Unsupported field type");
        }
    }


    private CustomFieldAnswer updateAnswer(CustomFieldAnswer managedAnswer, CustomFieldAnswer answer, CustomFieldAnswerId pk) {
        if (managedAnswer.getPk().getField() instanceof CustomFieldInteger) {
            ((CustomFieldAnswerInteger) managedAnswer).setValue(((CustomFieldAnswerInteger) answer).getValue());
        } else if (managedAnswer.getPk().getField() instanceof CustomFieldSelectMultiple) {
            CustomFieldAnswerSelectMultiple managedMultiAnswer = (CustomFieldAnswerSelectMultiple) managedAnswer;
            CustomFieldAnswerSelectMultiple newMultiAnswer = (CustomFieldAnswerSelectMultiple) answer;

            // Get the concepts from both answers
            Set<Concept> existingConcepts = new HashSet<>(managedMultiAnswer.getValue());
            Set<Concept> newConcepts = new HashSet<>(newMultiAnswer.getValue());

            // Find concepts to remove (in existing but not in new)
            Set<Concept> conceptsToRemove = new HashSet<>(existingConcepts);
            conceptsToRemove.removeAll(newConcepts);

            // Find concepts to add (in new but not in existing)
            Set<Concept> conceptsToAdd = new HashSet<>(newConcepts);
            conceptsToAdd.removeAll(existingConcepts);

            // Remove concepts that are no longer selected
            for (Concept concept : conceptsToRemove) {
                managedMultiAnswer.removeConcept(concept);
            }

            // Add newly selected concepts
            for (Concept concept : conceptsToAdd) {
                managedMultiAnswer.addConcept(concept);
            }
        } else {
            throw new IllegalArgumentException("Unsupported field type");
        }
        managedAnswer.setPk(pk);
        return managedAnswer;
    }

    // Process the form response and its answers. By removing answers that are not part of the form.
    public CustomFormResponse saveFormResponse(CustomFormResponse managedFormResponse, CustomFormResponse customFormResponse) {

        // get form (maybe only if it has changed)
        CustomForm managedForm = customFormRepository.findById(customFormResponse.getForm().getId()).get();
        managedFormResponse.setForm(managedForm);

        // Create a copy of the saved answer to keep track of the ones to delete
        Map<CustomField, CustomFieldAnswer> toBeDeleted = new HashMap<>(managedFormResponse.getAnswers());

        // Iterate over the form fields and look for answers to fields that are in the form
        for (CustomField managedField : managedForm.getFields()) {

            CustomFieldAnswer answer = customFormResponse.getAnswers().get(managedField); // get answer

            if (answer != null) {

                CustomFieldAnswerId pk = new CustomFieldAnswerId();
                pk.setFormResponse(managedFormResponse);
                pk.setField(managedField);

                // Get the answer if it already exist and modify it if necessary. Otherwise create it and add it.
                CustomFieldAnswer managedAnswer;
                if (managedFormResponse.getAnswers().containsKey(managedField)) {
                    managedAnswer = managedFormResponse.getAnswers().get(managedField);
                    // Update if necessary
                    if (hasValueChanged(pk.getField(), managedAnswer, answer)) {
                        updateAnswer(managedAnswer, answer, pk);
                    }
                } else {
                    // Create
                    managedAnswer = createAnswer(pk, answer);
                    managedFormResponse.addAnswer(managedAnswer);
                }

                // In both case, remove the answer from the list of answzr to be removed
                toBeDeleted.remove(managedField);

            }
        }

        // Delete the answer to be deleted
        for (CustomFieldAnswer managedAnswer : toBeDeleted.values()) {

            if (managedAnswer != null) {
                // Clear associations between the answer and the concept list if the type is select multiple
                if (managedAnswer instanceof CustomFieldAnswerSelectMultiple) {
                    ((CustomFieldAnswerSelectMultiple) managedAnswer).getValue().clear();
                }

                // Remove the answer to trigger its deletion
                managedFormResponse.getAnswers().remove(managedAnswer.getPk().getField());

            }
        }

        return managedFormResponse;
    }
}
