package fr.siamois.domain.services.form;

import fr.siamois.domain.models.form.customField.CustomField;
import fr.siamois.domain.models.form.customField.CustomFieldInteger;
import fr.siamois.domain.models.form.customField.CustomFieldSelectMultiple;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswerId;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswerInteger;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswerSelectMultiple;
import fr.siamois.domain.models.form.customFormField.CustomFormField;
import fr.siamois.domain.models.form.customFormResponse.CustomFormResponse;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.repositories.form.CustomFieldAnswerRepository;
import fr.siamois.infrastructure.repositories.form.CustomFieldRepository;
import fr.siamois.infrastructure.repositories.form.CustomFormResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomFormResponseService {
    private final CustomFormResponseRepository customFormResponseRepository;
    private final CustomFieldAnswerRepository customFieldAnswerRepository;
    private final CustomFieldRepository customFieldRepository;

    public CustomFormResponseService(CustomFormResponseRepository customFormResponseRepository, CustomFieldAnswerRepository customFieldAnswerRepository, CustomFieldRepository customFieldRepository) {
        this.customFormResponseRepository = customFormResponseRepository;
        this.customFieldAnswerRepository = customFieldAnswerRepository;
        this.customFieldRepository = customFieldRepository;
    }

    private CustomFieldAnswer createAnswer(CustomFieldAnswerId pk, CustomFieldAnswer answer) {
        CustomFieldAnswer createdAnswer;
        if (pk.getField() instanceof CustomFieldInteger) {
            createdAnswer = new CustomFieldAnswerInteger();
            ((CustomFieldAnswerInteger) createdAnswer).setValue(((CustomFieldAnswerInteger) answer).getValue());
        } else if (pk.getField() instanceof CustomFieldSelectMultiple) {
            createdAnswer = new CustomFieldAnswerSelectMultiple();
            ((CustomFieldAnswerSelectMultiple) createdAnswer).setConcepts(((CustomFieldAnswerSelectMultiple) answer).getConcepts());
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
                    ((CustomFieldAnswerSelectMultiple) existingAnswer).getConcepts().stream()
                            .map(Concept::getId)
                            .collect(Collectors.toSet()),

                    ((CustomFieldAnswerSelectMultiple) newValue).getConcepts().stream()
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
            Set<Concept> existingConcepts = new HashSet<>(managedMultiAnswer.getConcepts());
            Set<Concept> newConcepts = new HashSet<>(newMultiAnswer.getConcepts());

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
    @Transactional(propagation = Propagation.MANDATORY)
    public CustomFormResponse saveFormResponse(CustomFormResponse customFormResponse) {

        CustomFormResponse managedFormResponse;

        // Get the existing response or create a new one
        if (customFormResponse.getId() == null) {
            managedFormResponse = new CustomFormResponse();
        } else {
            Optional<CustomFormResponse> existingResponseOpt = customFormResponseRepository.findById(customFormResponse.getId());
            managedFormResponse = existingResponseOpt.orElseGet(CustomFormResponse::new);
        }

        managedFormResponse.setForm(customFormResponse.getForm());

        // Create a copy of the saved answer to keep track of the ones to delete
        Map<CustomField, CustomFieldAnswer> toBeDeleted = new HashMap<>(managedFormResponse.getAnswers());

        // Iterate over the form fields and look for answers to fields that are in the form
        for (CustomFormField formField : customFormResponse.getForm().getFields()) {

            // Is the field in the answer map?
            CustomField field = formField.getId().getField();

            CustomFieldAnswer answer = customFormResponse.getAnswers().get(field); // get answer

            if (answer != null) {

                // Get field from DB
                Optional<CustomField> optManagedField = customFieldRepository.findById(field.getId());
                CustomField managedField = optManagedField.get();

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
        for (CustomFieldAnswer answer : toBeDeleted.values()) {


            // Get a fresh reference to ensure it's managed
            CustomFieldAnswerId answerId = answer.getPk();
            CustomFieldAnswer managedAnswer = customFieldAnswerRepository.findByFormResponseIdAndFieldId(answerId.getFormResponse().getId(),
                    answerId.getField().getId()).orElse(null);

            if (managedAnswer != null) {
                // Clear associations between the answer and the concept list if the type is select multiple
                if (managedAnswer instanceof CustomFieldAnswerSelectMultiple) {
                    ((CustomFieldAnswerSelectMultiple) managedAnswer).getConcepts().clear();
                }

                // Remove it using the managed instance
                managedFormResponse.removeAnswer(managedAnswer);
                // Explicitly delete the answer from the repository
                // customFieldAnswerRepository.delete(managedAnswer);
            }
        }

        return managedFormResponse;
    }
}
