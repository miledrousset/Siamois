package fr.siamois.infrastructure.database.repositories.actionunit;

import fr.siamois.domain.models.actionunit.ActionCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ActionCodeRepository extends CrudRepository<ActionCode, String> {

    List<ActionCode>  findAllByCodeIsContainingIgnoreCase(String code);

}
