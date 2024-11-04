package fr.siamois.repositories;

import fr.siamois.models.Ark;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArkRepository extends CrudRepository<Ark, Integer> {

}
