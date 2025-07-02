package fr.siamois.domain.services;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.services.ark.ArkService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing ARK (Archival Resource Key) assignments to entities within an institution.
 * This service iterates through various entity services to assign ARKs to entities that do not have one.
 */
@Service
public class ArkManagerService {

    private final List<ArkEntityService> entityServices;
    private final InstitutionService institutionService;
    private final ArkService arkService;


    public ArkManagerService(List<ArkEntityService> entityServices,
                             InstitutionService institutionService,
                             ArkService arkService) {
        this.entityServices = entityServices;
        this.institutionService = institutionService;
        this.arkService = arkService;
    }

    /**
     * Adds ARK to entities that do not have one in the specified institution.
     *
     * @param institution the institution for which to add ARKs to entities
     */
    public void addArkToEntitiesWithoutArk(Institution institution) {
        InstitutionSettings settings = institutionService.createOrGetSettingsOf(institution);

        for (ArkEntityService service : entityServices) {
            List<? extends ArkEntity> entitesWithoutArk = service.findWithoutArk(institution);
            for (ArkEntity entity : entitesWithoutArk) {
                Ark ark = arkService.generateAndSave(settings);
                entity.setArk(ark);
                service.save(entity);
            }
        }

    }

}
