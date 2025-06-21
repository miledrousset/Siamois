package fr.siamois.domain.services.recordingunit;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.exceptions.recordingunit.MaxRecordingUnitIdentifierReached;
import fr.siamois.domain.models.exceptions.recordingunit.RecordingUnitNotFoundException;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.ArkEntityService;
import fr.siamois.domain.services.form.CustomFormResponseService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.recordingunit.RecordingUnitRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final CustomFormResponseService customFormResponseService;
    private final PersonRepository personRepository;


    public RecordingUnitService(RecordingUnitRepository recordingUnitRepository,
                                ConceptService conceptService,
                                CustomFormResponseService customFormResponseService,
                                PersonRepository personRepository) {
        this.recordingUnitRepository = recordingUnitRepository;
        this.conceptService = conceptService;
        this.customFormResponseService = customFormResponseService;
        this.personRepository = personRepository;
    }


    /**
     * Find all the recording units from a spatial unit
     *
     * @return The List of RecordingUnit
     * @throws RuntimeException If the repository method throws an Exception
     */
    @Transactional(readOnly = true)
    public List<RecordingUnit> findAllBySpatialUnit(SpatialUnit spatialUnit) {
        List<RecordingUnit> recordingUnits = recordingUnitRepository.findAllBySpatialUnitId(spatialUnit.getId());

        for (RecordingUnit recordingUnit : recordingUnits) {
            if (recordingUnit.getFormResponse() != null) {
                Hibernate.initialize(recordingUnit.getFormResponse().getAnswers());
            }
        }
        return recordingUnits;
    }

    /**
     * Find all the recording units from an action unit
     *
     * @return The List of RecordingUnit
     * @throws RuntimeException If the repository method throws an Exception
     */
    @Transactional(readOnly = true)
    public List<RecordingUnit> findAllByActionUnit(ActionUnit actionUnit) {
        return recordingUnitRepository.findAllByActionUnit(actionUnit);
    }

    public int generateNextIdentifier(RecordingUnit recordingUnit) {
        // Generate next identifier
        Integer currentMaxIdentifier = recordingUnitRepository.findMaxUsedIdentifierByAction(recordingUnit.getActionUnit().getId());
        int nextIdentifier = (currentMaxIdentifier == null) ? recordingUnit.getActionUnit().getMinRecordingUnitCode() : currentMaxIdentifier + 1;
        if (nextIdentifier > recordingUnit.getActionUnit().getMaxRecordingUnitCode() || nextIdentifier < 0) {
            throw new MaxRecordingUnitIdentifierReached("Max recording unit code reached; Please ask administrator to increase the range");
        }
        return (nextIdentifier);
    }

    @Transactional
    public int bulkUpdateType(List<Long> ids, Concept type) {
        return recordingUnitRepository.updateTypeByIds(type.getId(), ids);
    }

    @Transactional()
    public RecordingUnit save(RecordingUnit recordingUnit, Concept concept,
                              List<RecordingUnit> anteriorUnits,
                              List<RecordingUnit> synchronousUnits,
                              List<RecordingUnit> posteriorUnits) {

        try {

            RecordingUnit managedRecordingUnit;

            if (recordingUnit.getId() != null) {
                Optional<RecordingUnit> optRecordingUnit = recordingUnitRepository.findById(recordingUnit.getId());
                managedRecordingUnit = optRecordingUnit.orElseGet(RecordingUnit::new);
            } else {
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

            // Spatial Unit
            managedRecordingUnit.setSpatialUnit(recordingUnit.getSpatialUnit());

            // many to many (need managed instances)
            managedRecordingUnit.setAuthors((List<Person>) personRepository.findAllById(
                    recordingUnit.getAuthors().stream()
                            .map(Person::getId)
                            .toList()
                    )
            );
            managedRecordingUnit.setExcavators((List<Person>) personRepository.findAllById(
                            recordingUnit.getExcavators().stream()
                                    .map(Person::getId)
                                    .toList()
                    )
            );

            // Add other fields
            managedRecordingUnit.setAltitude(recordingUnit.getAltitude());
            managedRecordingUnit.setArk(recordingUnit.getArk());
            managedRecordingUnit.setDescription(recordingUnit.getDescription());
            managedRecordingUnit.setAuthor(recordingUnit.getAuthor());
            managedRecordingUnit.setEndDate(recordingUnit.getEndDate());
            managedRecordingUnit.setStartDate(recordingUnit.getStartDate());
            managedRecordingUnit.setSize(recordingUnit.getSize());
            managedRecordingUnit.setSecondaryType(recordingUnit.getSecondaryType());
            managedRecordingUnit.setThirdType(recordingUnit.getThirdType());
            managedRecordingUnit.setValidated(recordingUnit.getValidated());
            managedRecordingUnit.setValidatedAt(recordingUnit.getValidatedAt());
            managedRecordingUnit.setValidatedBy(recordingUnit.getValidatedBy());

            CustomFormResponse managedFormResponse;


            if (recordingUnit.getFormResponse() != null && recordingUnit.getFormResponse().getForm() != null) {
                // Save the form response if there is one

                // Get the existing response or create a new one
                if (managedRecordingUnit.getFormResponse() == null) {
                    // Initialize the managed form response
                    managedFormResponse = new CustomFormResponse();
                    managedRecordingUnit.setFormResponse(managedFormResponse);
                } else {
                    managedFormResponse = managedRecordingUnit.getFormResponse();
                }
                // Process form response
                customFormResponseService
                        .saveFormResponse(managedFormResponse, recordingUnit.getFormResponse());
            } else {
                managedRecordingUnit.setFormResponse(null);
            }


            return recordingUnitRepository.save(managedRecordingUnit);

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

    public long countByInstitution(Institution institution) {
        return recordingUnitRepository.countByCreatedByInstitution(institution);
    }

    @Transactional
    public Page<RecordingUnit> findAllByInstitutionAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
            Long institutionId,
            String fullIdentifier,
            Long[] categoryIds,
            String global,
            String langCode,
            Pageable pageable
    ) {
        Page<RecordingUnit> res = recordingUnitRepository.findAllByInstitutionAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                institutionId, fullIdentifier, categoryIds, global, langCode, pageable
        );


        // load related entities
        res.forEach(actionUnit -> {
            Hibernate.initialize(actionUnit.getParents());
            Hibernate.initialize(actionUnit.getChildren());

        });

        return res;
    }

    @Transactional
    public Page<RecordingUnit> findAllByInstitutionAndByActionUnitAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
            Long institutionId,
            Long actionId,
            String fullIdentifier,
            Long[] categoryIds,
            String global,
            String langCode,
            Pageable pageable
    ) {
        Page<RecordingUnit> res = recordingUnitRepository.findAllByInstitutionAndByActionUnitAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                institutionId, actionId, fullIdentifier, categoryIds, global, langCode, pageable
        );


        // load related entities
        res.forEach(actionUnit -> {
            Hibernate.initialize(actionUnit.getParents());
            Hibernate.initialize(actionUnit.getChildren());
        });

        return res;
    }

}


