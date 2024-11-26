package fr.siamois.infrastructure.repositories.ark;

import fr.siamois.models.ark.Ark;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArkRepository extends CrudRepository<Ark, String> {
    Optional<Ark> findArkByArkIdIgnoreCase(String arkId);
}
