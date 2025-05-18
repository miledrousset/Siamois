package fr.siamois.domain.services;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.institution.Institution;

import java.util.List;

public interface ArkEntityService {
    List<? extends ArkEntity> findWithoutArk(Institution institution);
    ArkEntity save(ArkEntity toSave);
}
