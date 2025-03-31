package fr.siamois.domain.services.ark;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.infrastructure.database.repositories.ArkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Service
public class ArkRedirectionService {

    private final ArkRepository arkRepository;
    private final SpatialUnitService spatialUnitService;
    private final ActionUnitService actionUnitService;
    private ServletUriComponentsBuilder builder;

    @Autowired
    public ArkRedirectionService(ArkRepository arkRepository,
                                 SpatialUnitService spatialUnitService,
                                 ActionUnitService actionUnitService
    ) {
        this.arkRepository = arkRepository;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
    }

    public ArkRedirectionService(ArkRepository arkRepository,
                                 SpatialUnitService spatialUnitService,
                                 ActionUnitService actionUnitService,
                                 ServletUriComponentsBuilder builder) {
        this.arkRepository = arkRepository;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.builder = builder;
    }

    public Optional<URI> getResourceUriFromArk(String naan, String qualifier) {
        Optional<Ark> optArk = arkRepository.findByNaanAndQualifier(naan, qualifier);
        if (optArk.isEmpty()) {
            return Optional.empty();
        }
        Ark ark = optArk.get();

        ServletUriComponentsBuilder currentBuilder;

        if (builder == null) {
            currentBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
        } else {
            currentBuilder = builder.cloneBuilder();
        }

        Optional<SpatialUnit> optSU = spatialUnitService.findByArk(ark);

        // TODO : open corresponding panel
//        if (optSU.isPresent()){
//            currentBuilder.path("/pages/spatialUnit/spatialUnit.xhtml")
//                    .queryParam("id", optSU.get().getId());
//            return Optional.of(currentBuilder.build().toUri());
//        }

        Optional<ActionUnit> optAU = actionUnitService.findByArk(ark);
        // todo : open corresponding panelm
//        if (optAU.isPresent()){
//            currentBuilder.path("/pages/actionUnit/actionUnit.xhtml")
//                    .queryParam("id", optAU.get().getId());
//            return Optional.of(currentBuilder.build().toUri());
//        }

        return Optional.empty();
    }

}
