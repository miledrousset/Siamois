package fr.siamois.domain.services.form;

import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.infrastructure.database.repositories.form.FormRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormService {

    private final FormRepository formRepository;

    public FormService(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    /**
     * Find a form by its ID
     *
     * @param id The ID of the form
     * @return The form having the given ID
     */
    @Transactional(readOnly = true)
    public CustomForm findById(long id) {

        return formRepository.findById(id).orElse(null);
    }


}
