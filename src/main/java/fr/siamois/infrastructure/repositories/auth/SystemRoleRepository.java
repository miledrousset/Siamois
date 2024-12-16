package fr.siamois.infrastructure.repositories.auth;

import fr.siamois.models.auth.SystemRole;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SystemRoleRepository extends CrudRepository<SystemRole, Long> {

    Optional<SystemRole> findSystemRoleByRoleNameIgnoreCase(String roleName);

}
