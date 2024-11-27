package fr.siamois.services;

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
     * Fetch the autocomplete results of Opentheso API for a given input and vocabulary collection.
     * @param input The input to search for
     * @return A list of concept field DTOs
     */
    public List<ConceptFieldDTO> fetchAutocomplete(List<VocabularyCollection> collections, String input) {
        List<ConceptFieldDTO> result = conceptApi.fetchAutocomplete(collections, input, "fr");
        if (result == null) return new ArrayList<>();
        return result;
    }

    public List<ConceptFieldDTO> fetchAutocomplete(Vocabulary vocabulary, String input) {
        List<ConceptFieldDTO> result = conceptApi.fetchAutocomplete(vocabulary, input, "fr");
        if (result == null) return new ArrayList<>();
        return result;
    }

    /**
     * Creates if not exists the arkServer, ark and concept for a given category and saves a new spatial unit with
     * the given hierarchy.
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

        Concept concept = saveOrGetConceptFromCategory(vocabulary, category);

        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setName(name);
        spatialUnit.setArk(spatialUnitArk);
        spatialUnit.setCategory(concept);

        spatialUnit = spatialUnitRepository.save(spatialUnit);

        return spatialUnit;
    }

    private Concept saveOrGetConceptFromCategory(Vocabulary vocabulary, ConceptFieldDTO category) {
        MultiValueMap<String,String> queryParams = UriComponentsBuilder.fromUriString(category.getUri()).build().getQueryParams();
        if (queryParams.containsKey("idt") && queryParams.containsKey("idc")) {
            String externalId = queryParams.get("idc").get(0);
            Optional<Concept> opt = conceptRepository.findConceptByExternalIdIgnoreCase(externalId);
            if (opt.isEmpty()) {
                Concept concept = new Concept();
                concept.setExternalId(externalId);
                concept.setVocabulary(vocabulary);
                concept.setLabel(category.getLabel());

                return conceptRepository.save(concept);
            } else {
                return opt.get();
            }
        } else {
            return processConceptWithArkUri(vocabulary, category);
        }
    }

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

    public List<SpatialUnit> fetchAllSpatialUnits() {
        Iterator<SpatialUnit> spatialUnitIterator = spatialUnitRepository.findAll().iterator();
        List<SpatialUnit> spatialUnits = new ArrayList<>();

        while (spatialUnitIterator.hasNext()) {
            spatialUnits.add(spatialUnitIterator.next());
        }

        return spatialUnits;
    }

    public SpatialUnit saveSpatialUnit(String fName, @NotNull Vocabulary vocabulary, ConceptFieldDTO selectedConceptFieldDTO, List<SpatialUnit> parentsSpatialUnit) throws SpatialUnitAlreadyExistsException {
        SpatialUnit unit = saveSpatialUnit(fName, vocabulary, selectedConceptFieldDTO);
        for (SpatialUnit parent : parentsSpatialUnit) {
            spatialUnitRepository.saveSpatialUnitHierarchy(parent.getId(), unit.getId());
        }
        return unit;
    }
}
