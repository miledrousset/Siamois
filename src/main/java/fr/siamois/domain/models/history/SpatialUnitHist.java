package fr.siamois.domain.models.history;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnitParent;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

@Deprecated
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "history_spatial_unit")
@Immutable
public class SpatialUnitHist extends SpatialUnitParent implements HistoryEntry<SpatialUnit> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(name = "spatial_unit_id", nullable = false)
    private Long tableId;

    @Column(name = "update_type", length = 10)
    @Enumerated(EnumType.STRING)
    private HistoryUpdateType updateType;

    @Column(name = "update_time")
    private OffsetDateTime updateTime;

}
