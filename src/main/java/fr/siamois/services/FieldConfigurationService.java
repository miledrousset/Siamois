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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service to handle the configuration of the field Spatial Unit in the application.
 * @author Julien Linget
 */
@Service
@Transactional
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
     * @param collectionsToSave The database saved collections
     * @throws FailedFieldUpdateException If the field already exists and the collection is the same
     * @throws FailedFieldSaveException If the save somehow failed
     */
    public void saveThesaurusCollectionFieldConfiguration(Person loggedUser,
                                                          String fieldCode,
                                                          List<VocabularyCollection> collectionsToSave) throws FailedFieldUpdateException, FailedFieldSaveException {
        Field field = prepareConfiguration(loggedUser, fieldCode, null, collectionsToSave);

        for (VocabularyCollection collection : collectionsToSave) {
            int result = fieldRepository.saveCollectionWithField(collection.getId(), field.getId());
            if (result == 0) throw new FailedFieldSaveException("Failed to save field. No row affected");
        }

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
        Field field = prepareConfiguration(loggedUser, fieldCode, vocabulary, null);

        int nbResult = fieldRepository.saveVocabularyWithField(field.getId(), vocabulary.getId());
        if (nbResult == 0) throw new FailedFieldSaveException("Failed to save field. No row affected");

    }

    /**
     * Deletes all existing configurations of a field and saves the new configuration.
     * @param loggedUser The database saved user
     * @param fieldCode The field code to save
     * @param vocabulary The database saved vocabulary
     * @param collections The database saved collections
     * @return The field to save
     * @throws FailedFieldUpdateException If the field already exists and the configuration is the same
     */
    private Field prepareConfiguration(Person loggedUser, String fieldCode, Vocabulary vocabulary, List<VocabularyCollection> collections) throws FailedFieldUpdateException {

        if (vocabulary == null) {
            deleteOldConfigurationIfDifferent(loggedUser, fieldCode, collections);
        } else {
            deleteOldConfigurationIfDifferent(loggedUser, fieldCode, vocabulary);
        }

        return getOrCreateField(loggedUser, fieldCode);
    }

    /**
     * Return the field if it exists, create it otherwise.
     * @param loggedUser The database saved user
     * @param fieldCode The field code to save
     * @return The database saved field
     * @throws FailedFieldSaveException If the save somehow failed
     */
    private Field getOrCreateField(Person loggedUser, String fieldCode) throws FailedFieldSaveException {
        Optional<Field> optField = fieldRepository.findByUserAndFieldCode(loggedUser, fieldCode);
        Field field;

        if (optField.isEmpty()) {
            field = new Field();
            field.setFieldCode(fieldCode);
            field.setUser(loggedUser);

            field = fieldRepository.save(field);
        } else {
            field = optField.get();
        }

        return field;
    }

    /**
     * Delete the old configuration when the new configuration is a vocabulary.
     * @param loggedUser The database saved user
     * @param fieldCode The field code to save
     * @param selectedVocab The database saved vocabulary
     * @throws FailedFieldUpdateException If the field already exists and the vocabulary is the same
     */
    private void  deleteOldConfigurationIfDifferent(Person loggedUser, String fieldCode, Vocabulary selectedVocab) throws FailedFieldUpdateException {
        fieldRepository.deleteVocabularyCollectionConfigurationByPersonAndFieldCode(loggedUser.getId(), fieldCode);

        Optional<Vocabulary> optVocab = vocabularyRepository.findVocabularyOfUserForField(loggedUser.getId(), fieldCode);
        if (optVocab.isPresent()) {
            if (optVocab.get().equals(selectedVocab)) {
                throw new FailedFieldUpdateException("Failed to update field : " + fieldCode);
            } else {
                fieldRepository.deleteVocabularyConfigurationByPersonAndFieldCode(loggedUser.getId(), fieldCode);
            }
        }
    }

    /**
     * Delete the old configuration when the new configuration is a collection.
     * @param loggedUser The database saved user
     * @param fieldCode The field code to save
     * @param collections The database saved collections
     * @throws FailedFieldUpdateException If the field already exists and the collection is the same
     */
    private void deleteOldConfigurationIfDifferent(Person loggedUser, String fieldCode, List<VocabularyCollection> collections) throws FailedFieldUpdateException {
        List<VocabularyCollection> existingCollections = vocabularyCollectionRepository.findAllVocabularyCollectionByPersonAndFieldCode(loggedUser.getId(), fieldCode);
        if (CollectionUtils.isEqualCollection(collections, existingCollections)) {
            throw new FailedFieldUpdateException("Failed to update field : " + fieldCode);
        }
        fieldRepository.deleteVocabularyCollectionConfigurationByPersonAndFieldCode(loggedUser.getId(), fieldCode);

        Optional<Vocabulary> optVocab = vocabularyRepository.findVocabularyOfUserForField(loggedUser.getId(), fieldCode);
        if (optVocab.isPresent()) {
            fieldRepository.deleteVocabularyConfigurationByPersonAndFieldCode(loggedUser.getId(), fieldCode);
        }
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
    public List<VocabularyCollection> fetchCollectionsOfPersonFieldConfiguration(Person loggedUser, String categoryFieldCode) {
        return vocabularyCollectionRepository.findAllVocabularyCollectionByPersonAndFieldCode(loggedUser.getId(), categoryFieldCode);
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
