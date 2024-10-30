package fr.siamois.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ark")
public class Ark {
    @Id
    @SequenceGenerator(name = "ark_id_gen", sequenceName = "concept_concept_id_seq", allocationSize = 1)
    @Column(name = "ark_id", nullable = false, length = Integer.MAX_VALUE)
    private String arkId;

}