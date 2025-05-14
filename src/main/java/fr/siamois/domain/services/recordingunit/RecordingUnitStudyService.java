package fr.siamois.domain.services.recordingunit;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnitStudy;
import fr.siamois.domain.services.ArkEntityService;
import fr.siamois.infrastructure.database.repositories.recordingunit.RecordingUnitStudyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordingUnitStudyService implements ArkEntityService {
    private final RecordingUnitStudyRepository recordingUnitStudyRepository;

    public RecordingUnitStudyService(RecordingUnitStudyRepository recordingUnitStudyRepository) {
        this.recordingUnitStudyRepository = recordingUnitStudyRepository;
    }

    @Override
    public List<? extends ArkEntity> findWithoutArk(Institution institution) {
        return recordingUnitStudyRepository.findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Override
    public ArkEntity save(ArkEntity toSave) {
        return recordingUnitStudyRepository.save((RecordingUnitStudy) toSave);
    }
}
