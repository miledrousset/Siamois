package fr.siamois.domain.services.specimen;

import fr.siamois.infrastructure.repositories.specimen.SpecimenStudyRepository;
import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.specimen.SpecimenStudy;
import fr.siamois.domain.services.ArkEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecimenStudyService implements ArkEntityService {

    private final SpecimenStudyRepository specimenStudyRepository;

    public SpecimenStudyService(SpecimenStudyRepository specimenStudyRepository) {
        this.specimenStudyRepository = specimenStudyRepository;
    }

    @Override
    public List<SpecimenStudy> findWithoutArk(Institution institution) {
        return specimenStudyRepository.findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Override
    public ArkEntity save(ArkEntity toSave) {
        return specimenStudyRepository.save((SpecimenStudy) toSave);
    }
}
