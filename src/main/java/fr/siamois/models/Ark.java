package fr.siamois.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ark")
public class Ark {
    @Id
    @Column(name = "ark_id", nullable = false, length = Integer.MAX_VALUE)
    private String id;

}