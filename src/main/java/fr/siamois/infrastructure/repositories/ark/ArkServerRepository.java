package fr.siamois.infrastructure.repositories.ark;

import fr.siamois.models.ark.ArkServer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArkServerRepository extends CrudRepository<ArkServer, Long> {

    /**
     * Find an ArkServer by its serverArkUri.
     * @param uri The serverArkUri to search for
     * @return An optional containing the ArkServer if found
     */
    Optional<ArkServer> findArkServerByServerArkUri(String uri);

    /**
     * Find the ArkServer with the flag isLocalServer set to true.
     * @return An optional containing the local ArkServer if found
     */
    @Query(
            nativeQuery = true,
            value = "SELECT * FROM ark_server WHERE is_local_server = TRUE LIMIT 1"
    )
    Optional<ArkServer> findLocalServer();
}
