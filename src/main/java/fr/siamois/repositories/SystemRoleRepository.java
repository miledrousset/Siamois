package fr.siamois.repositories;

import fr.siamois.models.Person;
import fr.siamois.models.SystemRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemRoleRepository extends CrudRepository<SystemRole, Integer> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM system_role_user sru JOIN system_role sr on sr.system_role_id = sru.role_id WHERE sru.person_id = :person"
    )
    List<SystemRole> findSystemRolesOfPerson(@Param("person") Person person);

}
