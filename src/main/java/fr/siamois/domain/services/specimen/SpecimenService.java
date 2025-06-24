package fr.siamois.domain.services.specimen;

import fr.siamois.domain.models.ArkEntity;

import fr.siamois.domain.models.institution.Institution;

import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.ArkEntityService;
import fr.siamois.infrastructure.database.repositories.specimen.SpecimenRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpecimenService implements ArkEntityService {

    private final SpecimenRepository specimenRepository;

    public SpecimenService(SpecimenRepository specimenRepository) {
        this.specimenRepository = specimenRepository;
    }


    @Override
    public List<Specimen> findWithoutArk(Institution institution) {
        return specimenRepository.findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    public int generateNextIdentifier(Specimen specimen) {
        // Generate next identifier
        Integer currentMaxIdentifier = specimenRepository.findMaxUsedIdentifierByRecordingUnit(specimen.getRecordingUnit().getId());
        int nextIdentifier = (currentMaxIdentifier == null) ? 1 : currentMaxIdentifier + 1;
        return (nextIdentifier);
    }

    public Specimen save(Specimen toSave) {

        if (toSave.getFullIdentifier() == null) {
            if (toSave.getIdentifier() == null) {

                toSave.setIdentifier(generateNextIdentifier(toSave));
            }
            // Set full identifier
            toSave.setFullIdentifier(toSave.displayFullIdentifier());
        }

        return specimenRepository.save(toSave);
    }

    @Override
    public ArkEntity save(ArkEntity toSave) {
        return specimenRepository.save((Specimen) toSave);
    }

    public Specimen findById(Long id) {
        return specimenRepository.findById(id).orElse(null);
    }

    @Transactional
    public Page<Specimen> findAllByInstitutionAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
            Long institutionId,
            String fullIdentifier,
            Long[] categoryIds,
            String global,
            String langCode,
            Pageable pageable
    ) {
        return specimenRepository.findAllByInstitutionAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                institutionId, fullIdentifier, categoryIds, global, langCode, pageable
        );

    }

    @Transactional
    public Page<Specimen> findAllByInstitutionAndByRecordingUnitAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
            Long institutionId,
            Long recordingUnitId,
            String fullIdentifier,
            Long[] categoryIds,
            String global,
            String langCode,
            Pageable pageable
    ) {
        return specimenRepository.findAllByInstitutionAndByRecordingUnitIdAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                institutionId, recordingUnitId, fullIdentifier, categoryIds, global, langCode, pageable
        );


    }

    @Transactional
    public int bulkUpdateType(List<Long> ids, Concept type) {
        return specimenRepository.updateTypeByIds(type.getId(), ids);
    }

    public long countByInstitution(Institution institution) {
        return specimenRepository.countByCreatedByInstitution(institution);
    }

}
