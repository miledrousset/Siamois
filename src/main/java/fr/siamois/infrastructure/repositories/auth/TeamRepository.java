package fr.siamois.infrastructure.repositories.auth;

import fr.siamois.domain.models.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {


}
