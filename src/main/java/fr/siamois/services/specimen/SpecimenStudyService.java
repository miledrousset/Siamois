package fr.siamois.services.specimen;

import fr.siamois.infrastructure.repositories.specimen.SpecimenStudyRepository;
import fr.siamois.models.ArkEntity;
import fr.siamois.models.Institution;
import fr.siamois.models.specimen.SpecimenStudy;
import fr.siamois.services.ArkEntityService;
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
