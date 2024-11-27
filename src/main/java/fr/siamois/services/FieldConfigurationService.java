package fr.siamois.services;

import fr.siamois.infrastructure.api.ThesaurusApi;
import fr.siamois.infrastructure.api.ThesaurusCollectionApi;
import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import fr.siamois.infrastructure.api.dto.VocabularyCollectionDTO;
import fr.siamois.infrastructure.repositories.FieldRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyCollectionRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyTypeRepository;
import fr.siamois.models.Field;
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

/**
 * Service to handle the configuration of the field Spatial Unit in the application.
 * @author Julien Linget
 */
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

    /**
     * Save the configuration of the field Spatial Unit for a user for a given collection.
     * Delete the old configuration if it exists, even if it's a VocabularyCollection or a Vocabulary.
     * @param loggedUser The database saved user
     * @param fieldCode The field code to save
     * @param collectionToSave The database saved collection to save
     * @throws FailedFieldUpdateException If the field already exists and the collection is the same
     * @throws FailedFieldSaveException If the save somehow failed
     */
    public void saveThesaurusCollectionFieldConfiguration(Person loggedUser,
                                                          String fieldCode,
                                                          VocabularyCollection collectionToSave) throws FailedFieldUpdateException, FailedFieldSaveException {
        boolean fieldExist = deleteOldConfigurationIfDifferent(loggedUser, fieldCode, collectionToSave);

        Field field = getOrCreateField(loggedUser, fieldCode, fieldExist);

        int result = fieldRepository.saveCollectionWithField(collectionToSave.getId(), field.getId());
        if (result == 0) throw new FailedFieldSaveException("Failed to save field. No row affected");
    }

    /**
     * Save the configuration of the field Spatial Unit for a user for a given vocabulary.
     * Delete the old configuration if it exists, even if it's a VocabularyCollection or a Vocabulary.
     * @param loggedUser The database saved user
     * @param fieldCode The field code to save
     * @param vocabulary The database saved vocabulary to save
     * @throws FailedFieldUpdateException If the field already exists and the vocabulary is the same
     * @throws FailedFieldSaveException If the save somehow failed
     */
    public void saveThesaurusFieldConfiguration(Person loggedUser,
                                                String fieldCode,
                                                Vocabulary vocabulary) throws FailedFieldUpdateException, FailedFieldSaveException {

        boolean fieldExist = deleteOldConfigurationIfDifferent(loggedUser, fieldCode, vocabulary);

        Field field = getOrCreateField(loggedUser, fieldCode, fieldExist);

        int nbResult = fieldRepository.saveVocabularyWithField(field.getId(), vocabulary.getId());
        if (nbResult == 0) throw new FailedFieldSaveException("Failed to save field. No row affected");

    }

    /**
     * Return the field if it exists, create it otherwise.
     * @param loggedUser The database saved user
     * @param fieldCode The field code to save
     * @param fieldExist True if the field already exists, false otherwise
     * @return The database saved field
     * @throws FailedFieldSaveException If the save somehow failed
     */
    private Field getOrCreateField(Person loggedUser, String fieldCode, boolean fieldExist) throws FailedFieldSaveException {
        Field field;
        if (fieldExist) {
            field = fieldRepository.findByUserAndFieldCode(loggedUser, fieldCode).orElseThrow(() -> new FailedFieldSaveException("Failed to save field"));
        } else {
            field = new Field();
            field.setFieldCode(fieldCode);
            field.setUser(loggedUser);

            field = fieldRepository.save(field);
        }
        return field;
    }

    /**
     * Delete the old configuration when the new configuration is a vocabulary.
     * @param loggedUser The database saved user
     * @param fieldCode The field code to save
     * @param selectedVocab The database saved vocabulary
     * @return True if the field exists, false otherwise
     * @throws FailedFieldUpdateException If the field already exists and the vocabulary is the same
     */
    private boolean deleteOldConfigurationIfDifferent(Person loggedUser, String fieldCode, Vocabulary selectedVocab) throws FailedFieldUpdateException, FailedFieldSaveException {
        boolean fieldExists = false;
        Optional<VocabularyCollection> optAlreadyExistConfigCollection = vocabularyCollectionRepository.findVocabularyCollectionByPersonAndFieldCode(loggedUser.getId(), fieldCode);
        if (optAlreadyExistConfigCollection.isPresent()) {
            fieldExists = true;
            int affectedRow = fieldRepository.deleteVocabularyCollectionConfigurationByPersonAndFieldCode(loggedUser.getId(), fieldCode);
            if (affectedRow == 0) throw new FailedFieldSaveException("Can't delete old configuration");
        }

        Optional<Vocabulary> optAlreadyExistConfigVocabulary = vocabularyRepository.findVocabularyOfUserForField(loggedUser.getId(), fieldCode);
        if (optAlreadyExistConfigVocabulary.isPresent()) {
            fieldExists = true;
            Vocabulary vocabulary = optAlreadyExistConfigVocabulary.get();
            if (vocabulary.equals(selectedVocab)) {
                throw new FailedFieldUpdateException("Failed to update field :" + fieldCode);
            } else {
                int affectedRow = fieldRepository.deleteVocabularyConfigurationByPersonAndFieldCode(loggedUser.getId(), fieldCode);
                if (affectedRow == 0) throw new FailedFieldSaveException("Can't delete old configuration");
            }
        }

        return fieldExists;
    }

    /**
     * Delete the old configuration when the new configuration is a collection.
     * @param loggedUser The database saved user
     * @param fieldCode The field code to save
     * @param selectedCollection The database saved collection
     * @return True if the field exists, false otherwise
     * @throws FailedFieldUpdateException If the field already exists and the collection is the same
     */
    private boolean deleteOldConfigurationIfDifferent(Person loggedUser, String fieldCode, VocabularyCollection selectedCollection) throws FailedFieldUpdateException {
        boolean fieldExists = false;
        Optional<VocabularyCollection> optAlreadyExistConfigCollection = vocabularyCollectionRepository.findVocabularyCollectionByPersonAndFieldCode(loggedUser.getId(), fieldCode);
        if (optAlreadyExistConfigCollection.isPresent()) {
            VocabularyCollection collection = optAlreadyExistConfigCollection.get();
            if (collection.equals(selectedCollection)) {
                throw new FailedFieldUpdateException("Failed to update field :" + fieldCode);
            } else {
                fieldExists = true;
                fieldRepository.deleteVocabularyCollectionConfigurationByPersonAndFieldCode(loggedUser.getId(), fieldCode);
            }
        }

        Optional<Vocabulary> optAlreadyExistConfigVocabulary = vocabularyRepository.findVocabularyOfUserForField(loggedUser.getId(), fieldCode);
        if (optAlreadyExistConfigVocabulary.isPresent()) {
            fieldExists = true;
            fieldRepository.deleteVocabularyConfigurationByPersonAndFieldCode(loggedUser.getId(), fieldCode);
        }

        return fieldExists;
    }

    /**
     * Record to store the collections and their labels.
     * @param collections The collections
     * @param localisedLabels The labels
     */
    public record VocabularyCollectionsAndLabels(List<VocabularyCollection> collections, List<String> localisedLabels) {}

    /**
     * Fetch all collections from a vocabulary and return them with their labels in the specified language.
     * @param lang The language to fetch the labels in
     * @param vocabulary The vocabulary to fetch the collections from
     * @return A record containing the collections and their labels
     * @throws ClientSideErrorException If the client sent wrong id or server URL
     */
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

    /**
     * Fetch the configuration of the field Spatial Unit for a user in the database.
     * @param loggedUser The user
     * @param categoryFieldCode The field code
     * @return The configuration if it exists
     */
    public Optional<VocabularyCollection> fetchCollectionOfPersonFieldConfiguration(Person loggedUser, String categoryFieldCode) {
        return vocabularyCollectionRepository.findVocabularyCollectionByPersonAndFieldCode(loggedUser.getId(), categoryFieldCode);
    }

    /**
     * Fetch the configuration of the field Spatial Unit for a user in the database.
     * @param loggedUser The database saved user
     * @param categoryFieldCode The field code
     * @return Optional of the configuration if it exists
     */
    public Optional<Vocabulary> fetchVocabularyOfPersonFieldConfiguration(Person loggedUser, String categoryFieldCode) {
        return vocabularyRepository.findVocabularyOfUserForField(loggedUser.getId(), categoryFieldCode);
    }

    /**
     * Fetch all public thesaurus name and labels from the API.
     * @param lang The language to fetch the labels in
     * @param serverUrl The server URL
     * @return A list of thesaurus
     */
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

    /**
     * Save a vocabulary if it does not exist in the database.
     * @param vocabulary The vocabulary to save
     * @return The saved vocabulary or the existing one if it exists
     */
    public Vocabulary saveVocabularyIfNotExists(Vocabulary vocabulary) {
        Optional<Vocabulary> opt = vocabularyRepository.findVocabularyByBaseUriAndVocabExternalId(vocabulary.getBaseUri(), vocabulary.getExternalVocabularyId());
        return opt.orElseGet(() -> vocabularyRepository.save(vocabulary));
    }

    /**
     * Save a vocabulary collection if it does not exist in the database.
     * @param vocabularyCollection The vocabulary collection to save
     * @return The saved vocabulary collection or the existing one if it exists
     */
    public VocabularyCollection saveVocabularyCollectionIfNotExists(VocabularyCollection vocabularyCollection) {
        Optional<VocabularyCollection> opt = vocabularyCollectionRepository.findByVocabularyAndExternalId(vocabularyCollection.getVocabulary(), vocabularyCollection.getExternalId());
        return opt.orElseGet(() -> vocabularyCollectionRepository.save(vocabularyCollection));
    }


}
