package fr.siamois.domain.services.form;

import fr.siamois.domain.models.form.Form;
import fr.siamois.infrastructure.repositories.form.FormRepository;
import org.springframework.stereotype.Service;

@Service
public class FormService {

    private final FormRepository formRepository;

    public FormService(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    /**
     * Find a form by its ID
     * @param id The ID of the form
     * @return The form having the given ID
     */
    public Form findById(long id) {
        return formRepository.findById(id).orElse(null);
    }
}
