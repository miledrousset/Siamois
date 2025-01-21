package fr.siamois.models;

import fr.siamois.models.auth.Person;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "creation_time")
    protected OffsetDateTime creationTime = OffsetDateTime.now(ZoneId.systemDefault());

    public abstract Long getId();
}
