package fr.siamois.models.history;

import fr.siamois.models.DocumentParent;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@Table(name = "history_siamois_document")
@Immutable
public class DocumentHist extends DocumentParent {

    @Id
    @Column(name = "history_id")
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long tableId;

    @Column(name = "update_type", length = 10)
    @Enumerated(EnumType.STRING)
    private HistoryUpdateType updateType;

    @Column(name = "update_time")
    private OffsetDateTime updateTime;

}
