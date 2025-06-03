package fr.siamois.infrastructure.database.repositories.team;

import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.team.ActionManagerRelation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionManagerRepository extends CrudRepository<ActionManagerRelation, ActionManagerRelation.ActionManagerId> {
    List<ActionManagerRelation> findAllByInstitution(Institution institution);
}
