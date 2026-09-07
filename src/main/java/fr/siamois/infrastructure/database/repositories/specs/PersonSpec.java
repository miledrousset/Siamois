package fr.siamois.infrastructure.database.repositories.specs;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.permissions.PersonProfileAssignment;
import fr.siamois.domain.models.permissions.Profile;
import fr.siamois.dto.entity.InstitutionDTO;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public final class PersonSpec {

    private PersonSpec() {
        throw new UnsupportedOperationException("PersonSpec should never be instantiated");
    }

    public static Specification<Person> isInInstitution(InstitutionDTO institution) {
        return ((personRoot, query, criteriaBuilder) -> {
            if (institution == null || institution.getId() == null) {
                return criteriaBuilder.conjunction();
            }
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<PersonProfileAssignment> ppaRoot = subquery.from(PersonProfileAssignment.class);
            Join<PersonProfileAssignment, Profile> profileJoin = ppaRoot.join("profile");
            subquery.select(criteriaBuilder.literal(1L));

            subquery.where(criteriaBuilder.and(
                    criteriaBuilder.equal(ppaRoot.get("person").get("id"), personRoot.get("id")),
                    criteriaBuilder.equal(profileJoin.get("institution").get("id"), institution.getId())
            ));

            return criteriaBuilder.exists(subquery);
        });
    }

    public static Specification<Person> firstNameOrLastNameContainsIgnoreCase(String name) {
        return ((root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + name.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastname")), pattern)
            );
        });
    }

    public static Specification<Person> emailContainsIgnoreCase(String email) {
        return ((root, query, criteriaBuilder) -> {
            if (email == null || email.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + email.toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern);
        });
    }

}
