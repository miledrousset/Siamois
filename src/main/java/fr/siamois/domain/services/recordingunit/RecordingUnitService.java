package fr.siamois.domain.services.recordingunit;

import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.exceptions.MaxRecordingUnitIdentifierReached;
import fr.siamois.domain.models.exceptions.RecordingUnitNotFoundException;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.ArkEntityService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to manage RecordingUnit
 *
 * @author Grégory Bliault
 */
@Slf4j
@Service
@Transactional
public class RecordingUnitService implements ArkEntityService {

    private final RecordingUnitRepository recordingUnitRepository;
    private final ConceptService conceptService;
    private final StratigraphicRelationshipService stratigraphicRelationshipService;

    public RecordingUnitService(RecordingUnitRepository recordingUnitRepository,
                                ConceptService conceptService,
                                StratigraphicRelationshipService stratigraphicRelationshipService) {
        this.recordingUnitRepository = recordingUnitRepository;
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

    public int generateNextIdentifier (RecordingUnit recordingUnit) {
        // Generate next identifier
        Integer currentMaxIdentifier = recordingUnitRepository.findMaxUsedIdentifierByAction(recordingUnit.getActionUnit().getId());
        int nextIdentifier = (currentMaxIdentifier == null) ? recordingUnit.getActionUnit().getMinRecordingUnitCode() : currentMaxIdentifier + 1;
        if (nextIdentifier > recordingUnit.getActionUnit().getMaxRecordingUnitCode() || nextIdentifier < 0) {
            throw new MaxRecordingUnitIdentifierReached("Max recording unit code reached; Please ask administrator to increase the range");
        }
        return(nextIdentifier);
    }

    @Transactional
    public RecordingUnit save(RecordingUnit recordingUnit, Concept concept,
                              List<RecordingUnit> anteriorUnits,
                              List<RecordingUnit> synchronousUnits,
                              List<RecordingUnit> posteriorUnits) {

        try {
            // Generate unique identifier if not present
            if (recordingUnit.getFullIdentifier() == null) {
                if (recordingUnit.getIdentifier() == null) {

                    recordingUnit.setIdentifier(generateNextIdentifier(recordingUnit));
                }
                // Set full identifier
                recordingUnit.setFullIdentifier(recordingUnit.displayFullIdentifier());
            }

            // Add concept
            Concept type = conceptService.saveOrGetConcept(concept);
            recordingUnit.setType(type);



            for (RecordingUnit syncUnit : synchronousUnits) {
                stratigraphicRelationshipService.saveOrGet(recordingUnit, syncUnit,
                        StratigraphicRelationshipService.SYNCHRONOUS);
            }

            for (RecordingUnit antUnit : anteriorUnits) {
                stratigraphicRelationshipService.saveOrGet(antUnit, recordingUnit,
                        StratigraphicRelationshipService.ASYNCHRONOUS);
            }

            for (RecordingUnit postUnit : posteriorUnits) {
                stratigraphicRelationshipService.saveOrGet(recordingUnit, postUnit,
                        StratigraphicRelationshipService.ASYNCHRONOUS);
            }

            recordingUnit = recordingUnitRepository.save(recordingUnit); // Reattach entity to get lazy props


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

    @Override
    public List<? extends ArkEntity> findWithoutArk(Institution institution) {
        return recordingUnitRepository.findAllWithoutArkOfInstitution(institution.getId());
    }

    @Override
    public ArkEntity save(ArkEntity toSave) {
        return recordingUnitRepository.save((RecordingUnit) toSave);
    }

}


