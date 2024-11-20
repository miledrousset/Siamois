package fr.siamois.services;

import fr.siamois.infrastructure.api.ThesaurusApi;
import fr.siamois.infrastructure.api.ThesaurusCollectionApi;
import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import fr.siamois.infrastructure.api.dto.VocabularyCollectionDTO;
import fr.siamois.infrastructure.repositories.FieldRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyCollectionRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyTypeRepository;
import fr.siamois.models.*;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.field.FailedFieldSaveException;
import fr.siamois.models.exceptions.field.FailedFieldUpdateException;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.models.vocabulary.VocabularyType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FieldConfigurationService {

    private final VocabularyTypeRepository vocabularyTypeRepository;
    private final ThesaurusApi thesaurusApi;
    private final VocabularyRepository vocabularyRepository;
    private final VocabularyCollectionRepository vocabularyCollectionRepository;
    private final FieldRepository fieldRepository;
    private final ThesaurusCollectionApi thesaurusCollectionApi;

    public FieldConfigurationService(VocabularyTypeRepository vocabularyTypeRepository,
                                     ThesaurusApi thesaurusApi,
                                     VocabularyRepository vocabularyRepository,
                                     VocabularyCollectionRepository vocabularyCollectionRepository,
                                     FieldRepository fieldRepository, ThesaurusCollectionApi thesaurusCollectionApi) {
        this.vocabularyTypeRepository = vocabularyTypeRepository;
        this.thesaurusApi = thesaurusApi;
        this.vocabularyRepository = vocabularyRepository;
        this.vocabularyCollectionRepository = vocabularyCollectionRepository;
        this.fieldRepository = fieldRepository;
        this.thesaurusCollectionApi = thesaurusCollectionApi;
    }

    public void saveThesaurusFieldConfiguration(Person loggedUser,
                                                String fieldCode,
                                                String serverUrl,
                                                String thesaurusId,
                                                VocabularyCollectionDTO selected) throws FailedFieldUpdateException, FailedFieldSaveException {

        VocabularyType type = vocabularyTypeRepository.findVocabularyTypeByLabel("Thesaurus").orElseThrow();

        ThesaurusDTO thesaurusDTO = thesaurusApi.fetchThesaurusInfos(serverUrl, thesaurusId).orElseThrow();

        Optional<Vocabulary> optVocab = vocabularyRepository.findVocabularyByBaseUriIgnoreCaseAndExternalVocabularyIdIgnoreCase(serverUrl, thesaurusId);

        Vocabulary vocabulary;

        if (optVocab.isPresent()) {
            vocabulary = optVocab.get();
        } else {
            vocabulary = new Vocabulary();
            vocabulary.setBaseUri(serverUrl);
            vocabulary.setExternalVocabularyId(thesaurusId);
            vocabulary.setType(type);
            vocabulary.setVocabularyName(thesaurusDTO.getLabels().get(0).getTitle());

            vocabulary = vocabularyRepository.save(vocabulary);
        }

        VocabularyCollection collection;

        Optional<VocabularyCollection> optCollection = vocabularyCollectionRepository.findByVocabularyAndExternalId(vocabulary, selected.getIdGroup());

        if (optCollection.isPresent()) {
            collection = optCollection.get();
        } else {
            collection = new VocabularyCollection();
            collection.setExternalId(selected.getIdGroup());
            collection.setVocabulary(vocabulary);

            vocabularyCollectionRepository.save(collection);
        }

        Field field;

        Optional<Field> optField = fieldRepository.findByUserAndFieldCode(loggedUser, SpatialUnit.CATEGORY_FIELD_CODE);
        Optional<VocabularyCollection> optCol = vocabularyCollectionRepository.findVocabularyCollectionByPersonAndFieldCode(loggedUser.getId(), SpatialUnit.CATEGORY_FIELD_CODE);

        if (optField.isPresent()) {
            field = optField.get();
            if (optCol.isPresent() && optCol.get().equals(collection)) {
                throw new FailedFieldUpdateException("Failed to update field :" + fieldCode);
            } else {
                fieldRepository.changeCollectionOfField(collection.getId(), field.getId());
            }
        } else {
            field = new Field();
            field.setUser(loggedUser);
            field.setFieldCode(fieldCode);

            field = fieldRepository.save(field);

            int nbResult = fieldRepository.saveCollectionWithField(collection.getId(), field.getId());
            if (nbResult == 0) throw new FailedFieldSaveException("Failed to save field");
        }
    }

    public List<VocabularyCollectionDTO> fetchListOfCollection(String serverUrl, String thesaurusId) {
        return thesaurusCollectionApi.fetchAllCollectionsFrom(serverUrl, thesaurusId);
    }

    public Optional<VocabularyCollection> fetchPersonFieldConfiguration(Person loggedUser, String categoryFieldCode) {
        return vocabularyCollectionRepository.findVocabularyCollectionByPersonAndFieldCode(loggedUser.getId(), categoryFieldCode);
    }
}
