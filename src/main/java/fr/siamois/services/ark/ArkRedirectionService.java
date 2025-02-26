package fr.siamois.services.ark;

import fr.siamois.infrastructure.repositories.ArkRepository;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.services.actionunit.ActionUnitService;
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
        if (optSU.isPresent()){
            currentBuilder.path("/pages/spatialUnit/spatialUnit.xhtml")
                    .queryParam("id", optSU.get().getId());
            return Optional.of(currentBuilder.build().toUri());
        }

        Optional<ActionUnit> optAU = actionUnitService.findByArk(ark);
        if (optAU.isPresent()){
            currentBuilder.path("/pages/actionUnit/actionUnit.xhtml")
                    .queryParam("id", optAU.get().getId());
            return Optional.of(currentBuilder.build().toUri());
        }

        return Optional.empty();
    }

}
