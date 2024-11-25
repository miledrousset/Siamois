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
import fr.siamois.models.exceptions.api.ClientSideErrorException;
import fr.siamois.models.exceptions.field.FailedFieldSaveException;
import fr.siamois.models.exceptions.field.FailedFieldUpdateException;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.models.vocabulary.VocabularyType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
                                                VocabularyCollection collectionToSave) throws FailedFieldUpdateException, FailedFieldSaveException {

        Field field;

        Optional<Field> optField = fieldRepository.findByUserAndFieldCode(loggedUser, SpatialUnit.CATEGORY_FIELD_CODE);
        Optional<VocabularyCollection> optCol = vocabularyCollectionRepository.findVocabularyCollectionByPersonAndFieldCode(loggedUser.getId(), SpatialUnit.CATEGORY_FIELD_CODE);

        if (optField.isPresent()) {
            field = optField.get();
            if (optCol.isPresent() && optCol.get().equals(collectionToSave)) {
                throw new FailedFieldUpdateException("Failed to update field :" + fieldCode);
            } else {
                fieldRepository.changeCollectionOfField(collectionToSave.getId(), field.getId());
            }
        } else {
            field = new Field();
            field.setUser(loggedUser);
            field.setFieldCode(fieldCode);

            field = fieldRepository.save(field);

            int nbResult = fieldRepository.saveCollectionWithField(collectionToSave.getId(), field.getId());
            if (nbResult == 0) throw new FailedFieldSaveException("Failed to save field");
        }
    }

    public record VocabularyCollectionsAndLabels(List<VocabularyCollection> collections, List<String> localisedLabels) {}

    public VocabularyCollectionsAndLabels fetchCollections(String lang, Vocabulary vocabulary) throws ClientSideErrorException {
        List<VocabularyCollectionDTO> dtos = thesaurusCollectionApi.fetchAllCollectionsFrom(vocabulary.getBaseUri(), vocabulary.getExternalVocabularyId());
        List<VocabularyCollection> result = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (VocabularyCollectionDTO dto : dtos) {
            Optional<VocabularyCollection> opt = vocabularyCollectionRepository.findByVocabularyAndExternalId(vocabulary, dto.getIdGroup());
            VocabularyCollection collection;
            if (opt.isEmpty()) {
                collection = new VocabularyCollection();
                collection.setId(-1L);
                collection.setExternalId(dto.getIdGroup());
                collection.setVocabulary(vocabulary);
            } else {
                collection = opt.get();
            }

            labels.add(dto.getLabels().stream()
                    .filter(labelDTO -> labelDTO.getLang().equalsIgnoreCase(lang))
                    .findFirst()
                    .orElseThrow()
                    .getTitle());

            result.add(collection);
        }

        return new VocabularyCollectionsAndLabels(result, labels);

    }

    public Optional<VocabularyCollection> fetchPersonFieldConfiguration(Person loggedUser, String categoryFieldCode) {
        return vocabularyCollectionRepository.findVocabularyCollectionByPersonAndFieldCode(loggedUser.getId(), categoryFieldCode);
    }

    public List<Vocabulary> fetchAllPublicThesaurus(String lang, String serverUrl) {
        List<Vocabulary> result = new ArrayList<>();
        List<ThesaurusDTO> dtos = thesaurusApi.fetchAllPublicThesaurus(serverUrl);
        VocabularyType type = vocabularyTypeRepository.findVocabularyTypeByLabel("Thesaurus").orElseThrow();

        for (ThesaurusDTO dto : dtos) {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setId(-1L);
            vocabulary.setBaseUri(serverUrl);
            vocabulary.setExternalVocabularyId(dto.getIdTheso());
            vocabulary.setVocabularyName(dto.getLabels().stream()
                    .filter(labelDTO -> labelDTO.getLang().equalsIgnoreCase(lang))
                    .findFirst()
                    .orElseThrow()
                    .getTitle()
            );
            vocabulary.setType(type);

            result.add(vocabulary);
        }

        return result;
    }

    public Vocabulary saveVocabularyIfNotExists(Vocabulary vocabulary) {
        Optional<Vocabulary> opt = vocabularyRepository.findVocabularyByBaseUriAndVocabExternalId(vocabulary.getBaseUri(), vocabulary.getExternalVocabularyId());
        return opt.orElseGet(() -> vocabularyRepository.save(vocabulary));
    }

    public VocabularyCollection saveVocabularyCollectionIfNotExists(VocabularyCollection vocabularyCollection) {
        Optional<VocabularyCollection> opt = vocabularyCollectionRepository.findByVocabularyAndExternalId(vocabularyCollection.getVocabulary(), vocabularyCollection.getExternalId());
        return opt.orElseGet(() -> vocabularyCollectionRepository.save(vocabularyCollection));
    }


}
