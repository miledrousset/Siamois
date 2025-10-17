package fr.siamois.domain.models.history;

import org.hibernate.envers.RevisionType;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;

public record RevisionWithInfo<T>(T entity, InfoRevisionEntity revisionEntity,
                                  RevisionType revisionType) implements Comparable<RevisionWithInfo<T>> {
    public OffsetDateTime getDate() {
        Instant instant = Instant.ofEpochMilli(revisionEntity.getTimestamp());
        return OffsetDateTime.ofInstant(instant, OffsetDateTime.now().getOffset());
    }

    @Override
    public int compareTo(RevisionWithInfo<T> o) {
        return Long.compare(revisionEntity.getTimestamp(), o.revisionEntity.getTimestamp());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RevisionWithInfo<?> that)) return false;
        return Objects.equals(revisionEntity, that.revisionEntity) && revisionType == that.revisionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(revisionEntity, revisionType);
    }
}