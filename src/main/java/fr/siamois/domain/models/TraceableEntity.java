package fr.siamois.domain.models;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * A {@link TraceableEntity} stores the creation time and the creator of an entity. The {@link TraceableEntity} is most likely to have a history
 * table in the database to store previous versions.
 *
 * @author Julien Linget
 */
@Setter
@Getter
@EqualsAndHashCode
@MappedSuperclass
public abstract class TraceableEntity implements Serializable {

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_author_id", nullable = false)
    protected Person author;

    @ColumnDefault("NOW()")
    @Column(name = "creation_time")
    protected OffsetDateTime creationTime = OffsetDateTime.now(ZoneId.systemDefault());

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_institution_id", nullable = false)
    protected Institution createdByInstitution;

    @ColumnDefault("NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_last_modification_person_id")
    protected Person lastModifiedBy = null;

    public abstract Long getId();
}
