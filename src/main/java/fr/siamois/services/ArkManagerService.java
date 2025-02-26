package fr.siamois.services;

import fr.siamois.models.ArkEntity;
import fr.siamois.models.Institution;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.settings.InstitutionSettings;
import fr.siamois.services.ark.ArkService;
import org.springframework.stereotype.Service;

import java.util.List;

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
