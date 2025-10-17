package fr.siamois.domain.models.history;

import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.models.specimen.SpecimenParent;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

@Deprecated
@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@Table(name = "history_specimen")
@Immutable
public class SpecimenHist extends SpecimenParent implements HistoryEntry<Specimen> {

    @Id
    @Column(name = "history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "specimen_id", nullable = false)
    private Long tableId;

    @Column(name = "update_type", length = 10)
    @Enumerated(EnumType.STRING)
    private HistoryUpdateType updateType;

    @Column(name = "update_time")
    private OffsetDateTime updateTime;

}
