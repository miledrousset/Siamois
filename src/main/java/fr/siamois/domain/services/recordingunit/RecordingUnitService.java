package fr.siamois.domain.services.recordingunit;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.exceptions.recordingunit.MaxRecordingUnitIdentifierReached;
import fr.siamois.domain.models.exceptions.recordingunit.RecordingUnitNotFoundException;
import fr.siamois.domain.models.form.customFormResponse.CustomFormResponse;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.ArkEntityService;
import fr.siamois.domain.services.form.CustomFormResponseService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.infrastructure.repositories.form.CustomFormResponseRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service to manage RecordingUnit
 *
 * @author Grégory Bliault
 */
@Slf4j
@Service
public class RecordingUnitService implements ArkEntityService {

    private final RecordingUnitRepository recordingUnitRepository;
    private final ConceptService conceptService;
    private final StratigraphicRelationshipService stratigraphicRelationshipService;
    private final CustomFormResponseService customFormResponseService;
    private final CustomFormResponseRepository customFormResponseRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public RecordingUnitService(RecordingUnitRepository recordingUnitRepository,
                                ConceptService conceptService,
                                StratigraphicRelationshipService stratigraphicRelationshipService,
                                CustomFormResponseService customFormResponseService, CustomFormResponseRepository customFormResponseRepository) {
        this.recordingUnitRepository = recordingUnitRepository;
        this.conceptService = conceptService;
        this.stratigraphicRelationshipService = stratigraphicRelationshipService;
        this.customFormResponseService = customFormResponseService;
        this.customFormResponseRepository = customFormResponseRepository;
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

    @Transactional()
    public RecordingUnit save(RecordingUnit recordingUnit, Concept concept,
                              List<RecordingUnit> anteriorUnits,
                              List<RecordingUnit> synchronousUnits,
                              List<RecordingUnit> posteriorUnits) {

        try {

            RecordingUnit managedRecordingUnit ;

            if(recordingUnit.getId() != null) {
                managedRecordingUnit = recordingUnitRepository.findById(recordingUnit.getId()).get();
            }
            else {
                managedRecordingUnit = new RecordingUnit();
            }

            // Generate unique identifier if not present
            managedRecordingUnit.setIdentifier(recordingUnit.getIdentifier());
            managedRecordingUnit.setFullIdentifier(recordingUnit.getFullIdentifier());
            managedRecordingUnit.setActionUnit(recordingUnit.getActionUnit());
            managedRecordingUnit.setCreatedByInstitution(recordingUnit.getCreatedByInstitution());
            if (managedRecordingUnit.getFullIdentifier() == null) {
                if (managedRecordingUnit.getIdentifier() == null) {

                    managedRecordingUnit.setIdentifier(generateNextIdentifier(managedRecordingUnit));
                }
                // Set full identifier
                managedRecordingUnit.setFullIdentifier(managedRecordingUnit.displayFullIdentifier());
            }

            // Add concept
            Concept type = conceptService.saveOrGetConcept(concept);
            managedRecordingUnit.setType(type);

            // Add other fields
            managedRecordingUnit.setAltitude(recordingUnit.getAltitude());
            managedRecordingUnit.setArk(recordingUnit.getArk());
            managedRecordingUnit.setDescription(recordingUnit.getDescription());
            managedRecordingUnit.setAuthor(recordingUnit.getAuthor());
            managedRecordingUnit.setExcavator(recordingUnit.getExcavator());
            managedRecordingUnit.setEndDate(recordingUnit.getEndDate());
            managedRecordingUnit.setStartDate(recordingUnit.getStartDate());
            managedRecordingUnit.setSize(recordingUnit.getSize());
            managedRecordingUnit.setSecondaryType(recordingUnit.getSecondaryType());

            CustomFormResponse managedFormResponse;

            // Get the existing response or create a new one
            if (managedRecordingUnit.getFormResponse() == null) {
                managedFormResponse = new CustomFormResponse();
                // Add a form response if no form response associated
                managedRecordingUnit.setFormResponse(managedFormResponse);
            } else {
                managedFormResponse = managedRecordingUnit.getFormResponse();
            }



            // Process form response
            customFormResponseService
                    .saveFormResponse(managedFormResponse, recordingUnit.getFormResponse());


            return managedRecordingUnit;

            //return recordingUnitRepository.save(managedRecordingUnit);
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


