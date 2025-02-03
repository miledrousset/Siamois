package fr.siamois.models;

import fr.siamois.models.auth.Person;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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
public abstract class TraceableEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_author_id", nullable = false)
    protected Person author;

    @ColumnDefault("NOW()")
    @Column(name = "creation_time")
    protected OffsetDateTime creationTime = OffsetDateTime.now(ZoneId.systemDefault());

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_institution_id")
    protected Institution createdByInstitution;

    @ColumnDefault("NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_last_modification_person_id")
    protected Person lastModifiedBy = null;

    public abstract Long getId();
}
