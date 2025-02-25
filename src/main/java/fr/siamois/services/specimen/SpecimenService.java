package fr.siamois.services.specimen;

import fr.siamois.infrastructure.repositories.specimen.SpecimenRepository;
import fr.siamois.models.ArkEntity;
import fr.siamois.models.Institution;
import fr.siamois.models.specimen.Specimen;
import fr.siamois.services.ArkEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecimenService implements ArkEntityService {

    private final SpecimenRepository specimenRepository;

    public SpecimenService(SpecimenRepository specimenRepository) {
        this.specimenRepository = specimenRepository;
    }

    @Override
    public List<? extends ArkEntity> findWithoutArk(Institution institution) {
        return specimenRepository.findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Override
    public ArkEntity save(ArkEntity toSave) {
        return specimenRepository.save((Specimen) toSave);
    }

}
