package fr.siamois.services.recordingunit;

import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitStudyRepository;
import fr.siamois.models.ArkEntity;
import fr.siamois.models.Institution;
import fr.siamois.models.recordingunit.RecordingUnitStudy;
import fr.siamois.services.ArkEntityService;
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
