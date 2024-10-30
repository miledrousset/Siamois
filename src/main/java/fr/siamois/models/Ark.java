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
    @Column(name = "ark_id", nullable = false, length = Integer.MAX_VALUE)
    private String id;

}