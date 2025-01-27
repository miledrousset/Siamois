package fr.siamois.services;

import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.models.vocabulary.Vocabulary;
import org.springframework.stereotype.Service;

@Service
public class ConceptService {

    private final ConceptApi conceptApi;

    public ConceptService(ConceptApi conceptApi) {
        this.conceptApi = conceptApi;
    }

    public void loadThesaurusSettings(Vocabulary vocabulary) {
        ConceptBranchDTO dto = conceptApi.fetchFieldsBranch(vocabulary);
    }

}
