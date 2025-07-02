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

/**
 * Service for handling ARK (Archival Resource Key) redirection.
 * This service provides methods to retrieve the resource URI based on the ARK identifier.
 */
@Service
public class ArkRedirectionService {

    private final ArkRepository arkRepository;
    private final SpatialUnitService spatialUnitService;
    private final ActionUnitService actionUnitService;
    private ServletUriComponentsBuilder builder;

    /**
     * Autowired constructor for ArkRedirectionService.
     *
     * @param arkRepository      the repository for ARK entities
     * @param spatialUnitService the service for spatial units
     * @param actionUnitService  the service for action units
     */
    @Autowired
    public ArkRedirectionService(ArkRepository arkRepository,
                                 SpatialUnitService spatialUnitService,
                                 ActionUnitService actionUnitService
    ) {
        this.arkRepository = arkRepository;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
    }

    /**
     * Constructor for ArkRedirectionService with a custom ServletUriComponentsBuilder for unit tests.
     *
     * @param arkRepository      the repository for ARK entities
     * @param spatialUnitService the service for spatial units
     * @param actionUnitService  the service for action units
     * @param builder            the ServletUriComponentsBuilder to use for building URIs
     */
    public ArkRedirectionService(ArkRepository arkRepository,
                                 SpatialUnitService spatialUnitService,
                                 ActionUnitService actionUnitService,
                                 ServletUriComponentsBuilder builder) {
        this.arkRepository = arkRepository;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.builder = builder;
    }

    /**
     * Retrieves the resource URI based on the ARK identifier.
     *
     * @param naan      the Name Assigning Authority Number (NAAN) of the ARK
     * @param qualifier the qualifier of the ARK
     * @return an Optional containing the resource URI if found, otherwise an empty Optional
     */
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

        if (optSU.isPresent()) {
            currentBuilder.path("/spatialunit")
                    .queryParam("id", optSU.get().getId());
            return Optional.of(currentBuilder.build().toUri());
        }

        Optional<ActionUnit> optAU = actionUnitService.findByArk(ark);
        if (optAU.isPresent()) {
            currentBuilder.path("/actionunit")
                    .queryParam("id", optAU.get().getId());
            return Optional.of(currentBuilder.build().toUri());
        }

        return Optional.empty();
    }

}
