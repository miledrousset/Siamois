package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "action_unit")
public class ActionUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_unit_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "begin_date", nullable = false)
    private OffsetDateTime beginDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private OffsetDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_type")
    private Concept type;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    private Ark ark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id")
    private fr.siamois.models.Person owner;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_spatial_unit_id", nullable = false)
    private SpatialUnit spatialUnit;

}