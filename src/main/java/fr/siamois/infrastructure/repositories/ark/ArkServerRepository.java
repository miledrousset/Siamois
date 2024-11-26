package fr.siamois.infrastructure.repositories.ark;

import fr.siamois.models.ark.ArkServer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArkServerRepository extends CrudRepository<ArkServer, Long> {

    Optional<ArkServer> findArkServerByServerArkUri(String uri);
    
}
