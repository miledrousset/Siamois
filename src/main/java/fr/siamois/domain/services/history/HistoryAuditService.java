package fr.siamois.domain.services.history;

import fr.siamois.domain.models.history.InfoRevisionEntity;
import fr.siamois.domain.models.history.RevisionWithInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryAuditService {

    private final AuditReader auditReader;

    @SuppressWarnings("unchecked")
    public <T> List<RevisionWithInfo<T>> findAllRevisionForEntity(Class<T> entityClass, Long entityId) {
        SortedSet<RevisionWithInfo<T>> results = new TreeSet<>();

        List<Object[]> rows = auditReader.createQuery()
                .forRevisionsOfEntity(entityClass, false, true)
                .add(AuditEntity.id().eq(entityId))
                .getResultList();

        for (Object[] row : rows) {
            RevisionWithInfo<T> info = new RevisionWithInfo<>(
                    (T) row[0],
                    (InfoRevisionEntity) row[1],
                    (RevisionType) row[2]
            );
            results.add(info);
        }

        return results.stream().toList();
    }

}
