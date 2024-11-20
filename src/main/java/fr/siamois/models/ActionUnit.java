package fr.siamois.models;

import fr.siamois.models.ark.Ark;
import fr.siamois.models.auth.Person;
import fr.siamois.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "action_unit")
public class ActionUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_unit_id", nullable = false)
    private Long id;

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
    private Person owner;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_spatial_unit_id", nullable = false)
    private SpatialUnit spatialUnit;

}