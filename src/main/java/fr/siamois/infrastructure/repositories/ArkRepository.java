package fr.siamois.infrastructure.repositories;

import fr.siamois.models.ark.Ark;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArkRepository extends CrudRepository<Ark, String> {

}
