package fr.siamois.domain.services.form;

import fr.siamois.domain.models.form.customField.CustomField;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswerId;
import fr.siamois.domain.models.form.customFormField.CustomFormField;
import fr.siamois.domain.models.form.customFormResponse.CustomFormResponse;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.infrastructure.repositories.form.CustomFormResponseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class CustomFormResponseService {
    private final CustomFormResponseRepository customFormResponseRepository;

    public CustomFormResponseService(CustomFormResponseRepository customFormResponseRepository) {
        this.customFormResponseRepository = customFormResponseRepository;
    }
    
    @Transactional
    public CustomFormResponse saveFormResponse(CustomFormResponse customFormResponse) {

        HashMap<CustomField, CustomFieldAnswer> answers = new HashMap<>();

        // Iterate over the form fields and look for submitted answer
        for (CustomFormField formField : customFormResponse.getForm().getFields()) {
            // Is the field in the answer map?
            CustomField field = formField.getId().getField();
            CustomFieldAnswer value = customFormResponse.getAnswers().get(field);
            if (value != null) {
                // If this field has been answered, add the answers
                value.setPk(new CustomFieldAnswerId());
                value.getPk().setFormResponse(customFormResponse);
                value.getPk().setField(field);
                answers.put(field, value);
            }
        }

        if (customFormResponse.getId() == null) {
            customFormResponse.setAnswers(answers);
            return customFormResponseRepository.save(customFormResponse);
        }

        Optional<CustomFormResponse> existingResponseOpt = customFormResponseRepository.findById(customFormResponse.getId());

        if (existingResponseOpt.isPresent()) {
            CustomFormResponse existingResponse = existingResponseOpt.get();

            // Clear previous answers
            existingResponse.getAnswers().clear();

            // Replace with new answers
            existingResponse.setAnswers(answers);

            return customFormResponseRepository.save(existingResponse);
        }

        // If new, just save it
        customFormResponse.setAnswers(answers);
        return customFormResponseRepository.save(customFormResponse);
    }
}
