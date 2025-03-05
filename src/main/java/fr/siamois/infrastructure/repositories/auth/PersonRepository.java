package fr.siamois.infrastructure.repositories.auth;

import fr.siamois.domain.models.auth.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    Optional<Person> findByUsernameIgnoreCase(String username);

    @Query(
            nativeQuery = true,
            value = "SELECT p.* FROM person p " +
                    "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :nameOrLastname, '%')) " +
                    "OR LOWER(p.lastname) LIKE LOWER(CONCAT('%', :nameOrLastname, '%'))"
    )
    List<Person> findAllByNameOrLastname(String nameOrLastname);

    Optional<Person> findById(long id);

    @Query(
            nativeQuery = true,
            value = "SELECT p.* FROM person p " +
                    "JOIN institution i ON p.person_id = i.fk_manager_id;"
    )
    List<Person> findAllInstitutionManagers();

    @Query(
            nativeQuery = true,
            value = "SELECT p.* FROM person p " +
                    "JOIN person_role_institution pri ON pri.fk_person_id = p.person_id " +
                    "WHERE pri.fk_institution_id = :institutionId"
    )
    List<Person> findMembersOfInstitution(Long institutionId);

    @Modifying
    @Transactional
    @Query(
            nativeQuery = true,
            value = "INSERT INTO person_role_institution(fk_person_id, fk_role_concept_id, fk_institution_id) " +
                    "VALUES (:personId, :conceptId, :institutionId)"
    )
    void addPersonToInstitution(Long personId, Long institutionId, Long conceptId);

    List<Person> findAllByIsSuperAdmin(Boolean isSuperAdmin);
}
