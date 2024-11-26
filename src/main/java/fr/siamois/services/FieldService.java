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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service to handle the fields in the application.
 * @author Julien Linget
 */
@Service
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
     * @param vocabularyCollection The database saved vocabulary collection
     * @param input The input to search for
     * @return A list of concept field DTOs
     */
    public List<ConceptFieldDTO> fetchAutocomplete(VocabularyCollection vocabularyCollection, String input) {
        List<ConceptFieldDTO> result = conceptApi.fetchAutocomplete(vocabularyCollection, input, "fr");
        if (result == null) return new ArrayList<>();
        return result;
    }

    /**
     * Creates if not exists the arkServer, ark and concept for a given category and saves a new spatial unit with
     * the given hierarchy.
     * @param name The name of the spatial unit
     * @param vocabulary The database saved vocabulary
     * @param category The API response for the category concept
     * @param parents The list of database saved parent spatial units
     * @param childs The list of database saved child spatial units
     * @return The saved spatial unit
     * @throws SpatialUnitAlreadyExistsException If a spatial unit with the same ARK already exists
     */
    public SpatialUnit saveSpatialUnit(String name,
                                       Vocabulary vocabulary,
                                       ConceptFieldDTO category,
                                       List<SpatialUnit> parents,
                                       List<SpatialUnit> childs) throws SpatialUnitAlreadyExistsException {

        SpatialUnit spatialUnit = saveSpatialUnit(name, vocabulary, category);

        for (SpatialUnit parent : parents) {
            spatialUnitRepository.saveSpatialUnitHierarchy(parent.getId(), spatialUnit.getId());
        }

        for (SpatialUnit child : childs) {
            spatialUnitRepository.saveSpatialUnitHierarchy(spatialUnit.getId(), child.getId());
        }

        return spatialUnit;
    }

    /**
     * Creates if not exists the arkServer, ark and concept for a given category and saves a new spatial unit with
     * the given hierarchy.
     * @param name The name of the spatial unit
     * @param vocabulary The database saved vocabulary
     * @param category The API response for the category concept
     * @return The saved spatial unit
     * @throws SpatialUnitAlreadyExistsException If a spatial unit with the same ARK already exists
     */
    public SpatialUnit saveSpatialUnit(String name,
                                       Vocabulary vocabulary,
                                       ConceptFieldDTO category) throws SpatialUnitAlreadyExistsException {
        String buildOpenthesoArkUri = vocabulary.getBaseUri() + "/openapi/v1/concept/ark:/";
        Optional<ArkServer> serverOpt = arkServerRepository.findArkServerByServerArkUri(buildOpenthesoArkUri);
        ArkServer srv;
        if (serverOpt.isEmpty()) {
            srv = new ArkServer();
            srv.setIsLocalServer(false);
            srv.setServerArkUri(buildOpenthesoArkUri);
            srv = arkServerRepository.save(srv);
        } else {
            srv = serverOpt.get();
        }

        String arkId = getArkIdFromUri(category.getUri());

        if (arkId.startsWith("/")) {
            arkId = arkId.substring(1);
        }

        Optional<Ark> arkOpt = arkRepository.findArkByArkIdIgnoreCase(arkId);
        Ark ark;
        if (arkOpt.isEmpty()) {
            ark = new Ark();
            ark.setArkId(arkId);
            ark.setArkServer(srv);
            ark = arkRepository.save(ark);
        } else {
            ark = arkOpt.get();
        }

        Optional<Concept> conceptOpt = conceptRepository.findConceptByArkId(ark.getArkId());
        Concept concept;
        if (arkOpt.isEmpty()) {
            concept = new Concept();
            concept.setArk(ark);
            concept.setVocabulary(vocabulary);
            concept.setLabel(category.getLabel());
            concept = conceptRepository.save(concept);
        } else {
            concept = conceptOpt.get();
        }

        Optional<SpatialUnit> spatialUnitOptional = spatialUnitRepository.findSpatialUnitByArkId(ark.getArkId());

        if (spatialUnitOptional.isPresent()) {
            throw new SpatialUnitAlreadyExistsException("A spatial unit with the same ARK already exists");
        }

        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setName(name);
        spatialUnit.setArk(ark);
        spatialUnit.setCategory(concept);

        spatialUnit = spatialUnitRepository.save(spatialUnit);

        return spatialUnit;
    }

    public boolean isSpatialUnitHierarchyCoherent(List<SpatialUnit> parents, List<SpatialUnit> children) {

        for (SpatialUnit parent : parents) {
            if (children.contains(parent)) {
                return false;
            }
        }

        for (SpatialUnit child : children) {
            if (parents.contains(child)) {
                return false;
            }
        }

        return true;
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

}
