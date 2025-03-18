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
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.infrastructure.repositories.form.CustomFieldAnswerRepository;
import fr.siamois.infrastructure.repositories.form.CustomFormResponseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomFormResponseService {
    private final CustomFormResponseRepository customFormResponseRepository;
    private final CustomFieldAnswerRepository customFieldAnswerRepository;

    public CustomFormResponseService(CustomFormResponseRepository customFormResponseRepository, CustomFieldAnswerRepository customFieldAnswerRepository) {
        this.customFormResponseRepository = customFormResponseRepository;
        this.customFieldAnswerRepository = customFieldAnswerRepository;
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
        return customFieldAnswerRepository.save(createdAnswer);
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

    private CustomFieldAnswer updateAnswer(CustomFieldAnswer existingAnswer, CustomFieldAnswer answer, CustomFieldAnswerId pk) {
        if (existingAnswer.getPk().getField() instanceof CustomFieldInteger) {
            ((CustomFieldAnswerInteger) existingAnswer).setValue(((CustomFieldAnswerInteger) answer).getValue());
        } else if (existingAnswer.getPk().getField() instanceof CustomFieldSelectMultiple) {
            ((CustomFieldAnswerSelectMultiple) existingAnswer).setConcepts(((CustomFieldAnswerSelectMultiple) answer).getConcepts());
        } else {
            throw new IllegalArgumentException("Unsupported field type");
        }
        existingAnswer.setPk(pk);
        return customFieldAnswerRepository.save(existingAnswer);
    }

    public CustomFormResponse saveFormResponse(CustomFormResponse customFormResponse) {

        CustomFormResponse savedResponse;

        if (customFormResponse.getId() == null) {
            savedResponse = customFormResponseRepository.save(customFormResponse);
        } else {
            Optional<CustomFormResponse> existingResponseOpt = customFormResponseRepository.findById(customFormResponse.getId());
            savedResponse = existingResponseOpt.orElseGet(() -> customFormResponseRepository.save(customFormResponse));
        }

        HashMap<CustomField, CustomFieldAnswer> answers = new HashMap<>();

        // Create a map of the existing answers by their field. They will be removed from here once created or updated
        Map<CustomField, CustomFieldAnswer> toBeDeleted = new HashMap<>(savedResponse.getAnswers());


        // Iterate over the form fields and look for submitted answer
/*        for (CustomFormField formField : customFormResponse.getForm().getFields()) {
            // Is the field in the answer map?
            CustomField field = formField.getId().getField();
            CustomFieldAnswer answer = customFormResponse.getAnswers().get(field);

            if (answer != null) {

                CustomFieldAnswerId pk = new CustomFieldAnswerId();
                pk.setFormResponse(savedResponse);
                pk.setField(field);
                Optional<CustomFieldAnswer> optionalAnswer = customFieldAnswerRepository.findByFormResponseIdAndFieldId(
                        pk.getFormResponse().getId(), pk.getField().getId()
                );
                CustomFieldAnswer newOrUpdatedAnswer = optionalAnswer.map(existingAnswer -> {
                    if (hasValueChanged(pk.getField(), existingAnswer, answer)) {
                        return updateAnswer(existingAnswer, answer, pk);
                    }
                    return existingAnswer;
                }).orElseGet(() -> createAnswer(pk, answer));


                answers.put(field, newOrUpdatedAnswer);
                toBeDeleted.remove(field); // Remove the updated or created answer from the existingAnswersMap
            }
        }*/

        // After processing new answers, delete the answers that were not updated or created
        // Delete obsolete answers
//        customFieldAnswerRepository.deleteAll(toBeDeleted.values());

        //savedResponse.setAnswers(answers);
        return savedResponse;
    }
}
