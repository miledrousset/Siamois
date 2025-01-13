package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.repositories.SpatialUnitRepository;
import fr.siamois.infrastructure.repositories.ark.ArkRepository;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.exceptions.SpatialUnitAlreadyExistsException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.services.ark.ArkGenerator;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Service to handle the fields in the application.
 * @author Julien Linget
 */
@Service
@Transactional
public class FieldService {

    private final ConceptApi conceptApi;
    private final ConceptRepository conceptRepository;
    private final ArkServerRepository arkServerRepository;
    private final ArkRepository arkRepository;
    private final SpatialUnitRepository spatialUnitRepository;

    public FieldService(ConceptApi conceptApi, ConceptRepository conceptRepository, ArkServerRepository arkServerRepository, ArkRepository arkRepository, SpatialUnitRepository spatialUnitRepository) {
        this.conceptApi = conceptApi;
        this.conceptRepository = conceptRepository;
        this.arkServerRepository = arkServerRepository;
        this.arkRepository = arkRepository;
        this.spatialUnitRepository = spatialUnitRepository;
    }

    /**
     * Fetch the autocomplete results of Opentheso API for a given input on multiple vocabulary collection.
     * @param input The input to search for
     * @param collections The list of database saved vocabulary collections to search in
     * @return A list of concept field DTOs
     */
    private List<ConceptFieldDTO> fetchAutocomplete(List<VocabularyCollection> collections, String input, String langCode) {
        List<ConceptFieldDTO> result = conceptApi.fetchAutocomplete(collections, input, langCode);
        if (result == null) return new ArrayList<>();
        return result;
    }

    /**
     * Fetch the autocomplete results of Opentheso API for a given input on a vocabulary.
     * @param vocabulary The database saved vocabulary to search in
     * @param input The input to search for
     * @return A list of concept field DTOs
     */
    private List<ConceptFieldDTO> fetchAutocomplete(Vocabulary vocabulary, String input, String langCode) {
        List<ConceptFieldDTO> result = conceptApi.fetchAutocomplete(vocabulary, input, langCode);
        if (result == null) return new ArrayList<>();
        return result;
    }

    /**
     * Fetch the autocomplete results of Opentheso API for a given input on a field configuration.
     * @param config The field configuration to search in
     * @param input The input to search for
     * @param langCode The language code to search in
     * @return A list of concept field DTOs
     */
    public List<ConceptFieldDTO> fetchAutocomplete(FieldConfigurationWrapper config, String input, String langCode) {
        if (config.vocabularyConfig() == null) {
            return fetchAutocomplete(config.vocabularyCollectionsConfig(), input, langCode);
        } else {
            return fetchAutocomplete(config.vocabularyConfig(), input, langCode);
        }
    }

    /**
     * Generate an ARK for a spatial unit. Create or save the category concept and save the spatial unit.
     * @param name The name of the spatial unit
     * @param vocabulary The database saved vocabulary
     * @param category The API response for the category concept
     * @return The saved spatial unit
     */
    public SpatialUnit saveSpatialUnit(String name,
                                       Vocabulary vocabulary,
                                       ConceptFieldDTO category) {

        ArkServer localServer = arkServerRepository.findLocalServer().orElseThrow(() -> new IllegalStateException("No local server found"));

        Ark spatialUnitArk = new Ark();
        spatialUnitArk.setArkId(ArkGenerator.generateArk());
        spatialUnitArk.setArkServer(localServer);
        spatialUnitArk = arkRepository.save(spatialUnitArk);

        Concept concept = saveOrGetConceptFromDto(vocabulary, category);

        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setName(name);
        spatialUnit.setArk(spatialUnitArk);
        spatialUnit.setCategory(concept);

        spatialUnit = spatialUnitRepository.save(spatialUnit);

        return spatialUnit;
    }


    /**
     * Save or get a concept from a category field DTO.
     * @param vocabulary The database saved vocabularies
     * @param conceptFieldDTO The API response for the concept
     * @return The saved concept
     */
    public Concept saveOrGetConceptFromDto(Vocabulary vocabulary, ConceptFieldDTO conceptFieldDTO) {
        MultiValueMap<String,String> queryParams = UriComponentsBuilder.fromUriString(conceptFieldDTO.getUri()).build().getQueryParams();
        if (queryParams.containsKey("idt") && queryParams.containsKey("idc")) {
            return processConceptWithExternalIdThesaurusAndIdConcept(vocabulary, conceptFieldDTO, queryParams);
        } else {
            return processConceptWithArkUri(vocabulary, conceptFieldDTO);
        }
    }

    /**
     * The save or get concept method for a category field DTO with an external ID and thesaurus ID.
     * @param vocabulary The database saved vocabulary
     * @param category The API response for the category concept
     * @param queryParams The query parameters of the URI
     * @return The saved concept
     */
    private Concept processConceptWithExternalIdThesaurusAndIdConcept(Vocabulary vocabulary, ConceptFieldDTO category, MultiValueMap<String, String> queryParams) {
        String conceptExternalId = queryParams.get("idc").get(0);
        Optional<Concept> opt = conceptRepository.findConceptByExternalIdIgnoreCase(vocabulary.getExternalVocabularyId(), conceptExternalId);
        if (opt.isEmpty()) {
            Concept concept = new Concept();
            concept.setExternalId(conceptExternalId);
            concept.setVocabulary(vocabulary);
            concept.setLabel(category.getLabel());

            return conceptRepository.save(concept);
        } else {
            return opt.get();
        }
    }

    /**
     * The save or get concept method for a category field DTO with an ARK URI.
     * @param vocabulary The database saved vocabulary
     * @param category The API response for the category concept
     * @return The saved concept
     */
    private Concept processConceptWithArkUri(Vocabulary vocabulary, ConceptFieldDTO category) {
        String buildOpenthesoArkUri = vocabulary.getBaseUri() + "/openapi/v1/concept/ark:/";
        Optional<ArkServer> serverOpt = arkServerRepository.findArkServerByServerArkUri(buildOpenthesoArkUri);
        ArkServer openthesoServer;
        if (serverOpt.isEmpty()) {
            openthesoServer = new ArkServer();
            openthesoServer.setIsLocalServer(false);
            openthesoServer.setServerArkUri(buildOpenthesoArkUri);
            openthesoServer = arkServerRepository.save(openthesoServer);
        } else {
            openthesoServer = serverOpt.get();
        }

        String conceptArkId = getArkIdFromUri(category.getUri());
        Optional<Ark> conceptArkOpt = arkRepository.findArkByArkIdIgnoreCase(conceptArkId);
        Ark conceptArk;
        if (conceptArkOpt.isEmpty()) {
            conceptArk = new Ark();
            conceptArk.setArkId(conceptArkId);
            conceptArk.setArkServer(openthesoServer);
            conceptArk = arkRepository.save(conceptArk);
        } else {
            conceptArk = conceptArkOpt.get();
        }

        Optional<Concept> conceptOpt = conceptRepository.findConceptByArkId(conceptArk.getArkId());
        Concept concept;
        if (conceptOpt.isEmpty()) {
            concept = new Concept();
            concept.setArk(conceptArk);
            concept.setVocabulary(vocabulary);
            concept.setLabel(category.getLabel());
            concept = conceptRepository.save(concept);
        } else {
            concept = conceptOpt.get();
        }
        return concept;
    }

    /**
     * Extract the ARK ID from an URI.
     * @param uri The URI to extract the ARK ID from
     * @return The ARK ID with the format "naan/arkId" without the "ark:/" prefix
     */
    public String getArkIdFromUri(String uri) {
        StringBuilder builder = new StringBuilder();
        int slashCount = 0;
        int i = uri.length() - 1;
        while (i >= 0 && slashCount < 2) {
            if (uri.charAt(i) == '/') {
                slashCount++;
            }

            if (slashCount < 2) {
                builder.insert(0, uri.charAt(i));
            }

            i--;
        }
        return builder.toString();
    }

    /**
     * Fetch all the spatial units in the database.
     * @return A list of all the spatial units
     */
    public List<SpatialUnit> fetchAllSpatialUnits() {
        Iterator<SpatialUnit> spatialUnitIterator = spatialUnitRepository.findAll().iterator();
        List<SpatialUnit> spatialUnits = new ArrayList<>();

        while (spatialUnitIterator.hasNext()) {
            spatialUnits.add(spatialUnitIterator.next());
        }

        return spatialUnits;
    }

    /**
     * Save the spatial unit and its hierarchy.
     * @param fName The name of the spatial unit
     * @param vocabulary The database saved vocabulary
     * @param selectedConceptFieldDTO The API response for the category concept
     * @param parentsSpatialUnit The list of database saved parent spatial units
     * @return The saved spatial unit
     * @throws SpatialUnitAlreadyExistsException If the spatial unit already exists in the database
     */
    public SpatialUnit saveSpatialUnit(String fName, @NotNull Vocabulary vocabulary, ConceptFieldDTO selectedConceptFieldDTO, List<SpatialUnit> parentsSpatialUnit) throws SpatialUnitAlreadyExistsException {
        SpatialUnit unit = saveSpatialUnit(fName, vocabulary, selectedConceptFieldDTO);
        for (SpatialUnit parent : parentsSpatialUnit) {
            spatialUnitRepository.saveSpatialUnitHierarchy(parent.getId(), unit.getId());
        }
        return unit;
    }

    /**
     * Save a concept if it does not exist in the database.
     * @param fieldConfig The field configuration to save the concept in
     * @param dto The API response for the concept
     * @return The saved concept
     */
    public Concept saveConceptIfNotExist(FieldConfigurationWrapper fieldConfig, ConceptFieldDTO dto) {
        Vocabulary vocabulary = fieldConfig.vocabularyConfig();
        if (vocabulary == null) vocabulary = fieldConfig.vocabularyCollectionsConfig().get(0).getVocabulary();

        return saveOrGetConceptFromDto(vocabulary, dto);
    }
}
