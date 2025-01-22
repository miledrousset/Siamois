package fr.siamois.infrastructure.repositories.ark;

import fr.siamois.models.ark.Ark;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArkRepository extends CrudRepository<Ark, String> {

    /**
     * Find an ark by its arkId ignoring case.
     * @param arkId The arkId to search for with the format "naan/arkId"
     * @return An optional containing the ark if found
     */
    Optional<Ark> findArkByArkIdIgnoreCase(String arkId);
}
