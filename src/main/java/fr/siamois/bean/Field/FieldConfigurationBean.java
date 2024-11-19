package fr.siamois.bean.Field;

import fr.siamois.infrastructure.api.ThesaurusApi;
import fr.siamois.infrastructure.api.ThesaurusCollectionApi;
import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import fr.siamois.infrastructure.api.dto.VocabularyCollectionDTO;
import fr.siamois.infrastructure.repositories.FieldRepository;
import fr.siamois.infrastructure.repositories.VocabularyCollectionRepository;
import fr.siamois.infrastructure.repositories.VocabularyRepository;
import fr.siamois.infrastructure.repositories.VocabularyTypeRepository;
import fr.siamois.models.*;
import fr.siamois.utils.AuthenticatedUserUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class FieldConfigurationBean implements Serializable {

    private final AuthenticatedUserUtils userUtils = new AuthenticatedUserUtils();

    private String serverUrl = "https://thesaurus.mom.fr/opentheso";
    private String thesaurusId = "th221";
    private String selectedValue = "";
    private List<String> values = new ArrayList<>();
    private List<VocabularyCollectionDTO> vocabularyCollectionDTOS;

    private final ThesaurusCollectionApi thesaurusCollectionApi;
    private final ThesaurusApi thesaurusApi;

    private final FieldRepository fieldRepository;
    private final VocabularyCollectionRepository vocabularyCollectionRepository;
    private final VocabularyRepository vocabularyRepository;
    private final VocabularyTypeRepository vocabularyTypeRepository;

    public FieldConfigurationBean(ThesaurusCollectionApi thesaurusCollectionApi,
                                  ThesaurusApi thesaurusApi,
                                  FieldRepository fieldRepository,
                                  VocabularyCollectionRepository vocabularyCollectionRepository,
                                  VocabularyRepository vocabularyRepository,
                                  VocabularyTypeRepository vocabularyTypeRepository) {
        this.thesaurusCollectionApi = thesaurusCollectionApi;
        this.thesaurusApi = thesaurusApi;
        this.fieldRepository = fieldRepository;
        this.vocabularyCollectionRepository = vocabularyCollectionRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.vocabularyTypeRepository = vocabularyTypeRepository;
    }

    public String getAuthenticatedUser() {
        Person loggedUser = userUtils.getAuthenticatedUser().orElseThrow();
        return loggedUser.getUsername();
    }

    public void processForm() {
        Person loggedUser = userUtils.getAuthenticatedUser().orElseThrow();
        VocabularyCollectionDTO selected = findSelectedCollection().orElseThrow();

        VocabularyType type = vocabularyTypeRepository.findVocabularyTypeByLabel("Thesaurus").orElseThrow();
        ThesaurusDTO thesaurusDTO = thesaurusApi.fetchThesaurusInfos(serverUrl, thesaurusId).orElseThrow();

        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setBaseUri(serverUrl);
        vocabulary.setExternalVocabularyId(thesaurusId);
        vocabulary.setType(type);
        vocabulary.setVocabularyName(thesaurusDTO.getLabels().get(0).getTitle());

        vocabulary = vocabularyRepository.save(vocabulary);

        VocabularyCollection collection = new VocabularyCollection();
        collection.setExternalId(selected.getIdGroup());
        collection.setVocabulary(vocabulary);

        collection = vocabularyCollectionRepository.save(collection);

        Field field1 = new Field();
        field1.setUser(loggedUser);
        field1.setFieldCode(SpatialUnit.CATEGORY_FIELD_CODE);

        field1 = fieldRepository.save(field1);

        boolean result = fieldRepository.saveFieldWithCollection(collection.getId(), field1.getId());
        if (!result) throw new RuntimeException("Failed to save field");

    }

    public void loadValues() {
        vocabularyCollectionDTOS = thesaurusCollectionApi.fetchAllCollectionsFrom(serverUrl, thesaurusId);
        for (VocabularyCollectionDTO data : vocabularyCollectionDTOS) {
            values.add(data.getLabels().get(0).getTitle());
        }
    }

    private boolean collectionDTOContainsLabel(VocabularyCollectionDTO dto, String label) {
        return dto.getLabels()
                .stream()
                .anyMatch((labelDTO -> labelDTO.getTitle().equals(label)));
    }

    private Optional<VocabularyCollectionDTO> findSelectedCollection() {
        int i = 0;
        while (i < vocabularyCollectionDTOS.size() && !collectionDTOContainsLabel(vocabularyCollectionDTOS.get(i), selectedValue)) {
            i++;
        }
        if (i == vocabularyCollectionDTOS.size()) return Optional.empty();
        return Optional.of(vocabularyCollectionDTOS.get(i));
    }

}
