package fr.siamois.domain.models.settings;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "person_role_institution")
public class PersonRoleInstitution {
    @EmbeddedId
    private PersonRoleInstitutionId id;

    @MapsId("fkPersonId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_person_id", nullable = false)
    private Person person;

    @MapsId("fkInstitutionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_institution_id", nullable = false)
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_role_concept_id")
    private Concept roleConcept;

    @ColumnDefault("false")
    @Column(name = "is_manager")
    private Boolean isManager;

    @ColumnDefault("NOW()")
    @Column(name = "added_at")
    private OffsetDateTime addedAt = OffsetDateTime.now();

    @Getter
    @Setter
    @Embeddable
    public static class PersonRoleInstitutionId implements java.io.Serializable {
        @Serial
        private static final long serialVersionUID = -4502700731210054365L;
        @NotNull
        @Column(name = "fk_person_id", nullable = false)
        private Long fkPersonId;

        @NotNull
        @Column(name = "fk_institution_id", nullable = false)
        private Long fkInstitutionId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
            PersonRoleInstitutionId entity = (PersonRoleInstitutionId) o;
            return Objects.equals(this.fkPersonId, entity.fkPersonId) &&
                    Objects.equals(this.fkInstitutionId, entity.fkInstitutionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fkPersonId, fkInstitutionId);
        }

    }

}