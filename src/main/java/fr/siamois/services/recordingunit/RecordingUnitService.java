package fr.siamois.services.recordingunit;

import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.models.exceptions.MaxRecordingUnitIdentifierReached;
import fr.siamois.models.exceptions.RecordingUnitNotFoundException;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.StratigraphicRelationship;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.vocabulary.ConceptService;
import fr.siamois.utils.ArkGeneratorUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service to manage RecordingUnit
 *
 * @author Grégory Bliault
 */
@Slf4j
@Service
@Transactional
public class RecordingUnitService {

    private final RecordingUnitRepository recordingUnitRepository;
    private final ArkServerRepository arkServerRepository;
    private final ConceptService conceptService;
    private final StratigraphicRelationshipService stratigraphicRelationshipService;

    public RecordingUnitService(RecordingUnitRepository recordingUnitRepository,
                                ArkServerRepository arkServerRepository,
                                ConceptService conceptService, StratigraphicRelationshipService stratigraphicRelationshipService) {
        this.recordingUnitRepository = recordingUnitRepository;
        this.arkServerRepository = arkServerRepository;
        this.conceptService = conceptService;
        this.stratigraphicRelationshipService = stratigraphicRelationshipService;
    }


    /**
     * Find all the recording units from a spatial unit
     *
     * @return The List of RecordingUnit
     * @throws RuntimeException If the repository method throws an Exception
     */
    public List<RecordingUnit> findAllBySpatialUnit(SpatialUnit spatialUnit) {
        return recordingUnitRepository.findAllBySpatialUnitId(spatialUnit.getId());
    }

    /**
     * Find all the recording units from an action unit
     *
     * @return The List of RecordingUnit
     * @throws RuntimeException If the repository method throws an Exception
     */
    public List<RecordingUnit> findAllByActionUnit(ActionUnit actionUnit) {
        return recordingUnitRepository.findAllByActionUnit(actionUnit);
    }

    @Transactional
    public RecordingUnit save(RecordingUnit recordingUnit, Concept concept,
                              List<RecordingUnit> anteriorUnits,
                              List<RecordingUnit> synchronousUnits,
                              List<RecordingUnit> posteriorUnits) {

        try {
            // Generate ARK if the recording unit does not have any
            if (recordingUnit.getArk() == null) {

                ArkServer localServer = arkServerRepository.findLocalServer().orElseThrow(() -> new IllegalStateException("No local server found"));
                Ark ark = new Ark();
                ark.setArkServer(localServer);
                ark.setArkId(ArkGeneratorUtils.generateArk());
                recordingUnit.setArk(ark);
            }

            // Generate unique identifier if not present
            if (recordingUnit.getFullIdentifier() == null) {
                if (recordingUnit.getIdentifier() == null) {
                    // Generate next identifier
                    Integer currentMaxIdentifier = recordingUnitRepository.findMaxUsedIdentifierByAction(recordingUnit.getActionUnit().getId());
                    Integer nextIdentifier = (currentMaxIdentifier == null) ? recordingUnit.getActionUnit().getMinRecordingUnitCode() : currentMaxIdentifier + 1;
                    if (nextIdentifier > recordingUnit.getActionUnit().getMaxRecordingUnitCode() || nextIdentifier < 0) {
                        throw new MaxRecordingUnitIdentifierReached("Max recording unit code reached; Please ask administrator to increase the range");
                    }
                    recordingUnit.setIdentifier(nextIdentifier);
                }
                // Set full identifier
                recordingUnit.setFullIdentifier(recordingUnit.displayFullIdentifier());
            }

            // Add concept
            Concept type = conceptService.saveOrGetConcept(concept);
            recordingUnit.setType(type);

            // Handle strati relationships
            // Step 1: Clear existing relationships (optional, if updating)
//            recordingUnit.getRelationshipsAsUnit1().clear();
//            recordingUnit.getRelationshipsAsUnit2().clear();

            // Step 2: This will contain the new relationships
            Set<StratigraphicRelationship> newRelationships = new HashSet<>();

            for (RecordingUnit syncUnit : synchronousUnits) {
                newRelationships.add(stratigraphicRelationshipService.saveOrGet(recordingUnit, syncUnit,
                        StratigraphicRelationshipService.SYNCHRONOUS));
            }

            for (RecordingUnit antUnit : anteriorUnits) {
                newRelationships.add(stratigraphicRelationshipService.saveOrGet(antUnit, recordingUnit,
                        StratigraphicRelationshipService.ASYNCHRONOUS));
            }

            for (RecordingUnit postUnit : posteriorUnits) {
                newRelationships.add(stratigraphicRelationshipService.saveOrGet(recordingUnit, postUnit,
                        StratigraphicRelationshipService.ASYNCHRONOUS));
            }

            recordingUnit = recordingUnitRepository.save(recordingUnit); // Reattach entity to get lazy props

            // Step 3: Add relationships to RecordingUnit
            Hibernate.initialize(recordingUnit.getRelationshipsAsUnit1());
            Hibernate.initialize(recordingUnit.getRelationshipsAsUnit2());

            for (StratigraphicRelationship rel : newRelationships) {
                if (rel.getUnit1().equals(recordingUnit)) {
                    recordingUnit.getRelationshipsAsUnit1().add(rel);
                } else {
                    recordingUnit.getRelationshipsAsUnit2().add(rel);
                }
            }

            return recordingUnitRepository.save(recordingUnit);
        } catch (RuntimeException e) {
            throw new FailedRecordingUnitSaveException(e.getMessage());
        }
    }

    /**
     * Find a recording unit by its ID
     *
     * @param id The ID of the recording unit
     * @return The RecordingUnit having the given ID
     * @throws RecordingUnitNotFoundException If no recording unit are found for the given id
     * @throws RuntimeException               If the repository method returns a RuntimeException
     */
    public RecordingUnit findById(long id) {
        try {
            return recordingUnitRepository.findById(id).orElseThrow(() -> new RecordingUnitNotFoundException("RecordingUnit not found with ID: " + id));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

}


