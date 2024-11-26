package fr.siamois.infrastructure.repositories.ark;

import fr.siamois.models.ark.ArkServer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArkServerRepository extends CrudRepository<ArkServer, Long> {

    Optional<ArkServer> findArkServerByServerArkUri(String uri);

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM ark_server WHERE is_local_server = TRUE LIMIT 1"
    )
    Optional<ArkServer> findLocalServer();
}
