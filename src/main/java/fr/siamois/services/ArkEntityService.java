package fr.siamois.services;

import fr.siamois.models.ArkEntity;
import fr.siamois.models.Institution;

import java.util.List;

public interface ArkEntityService {
    List<? extends ArkEntity> findWithoutArk(Institution institution);
    ArkEntity save(ArkEntity toSave);
}
