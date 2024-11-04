package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ark", schema = "public")
public class Ark {
    @Id
    @Column(name = "ark_id", nullable = false, length = Integer.MAX_VALUE)
    private String arkId;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_base_uri", nullable = false, referencedColumnName = "server_ark_uri")
    private ArkServer arkServer;

}