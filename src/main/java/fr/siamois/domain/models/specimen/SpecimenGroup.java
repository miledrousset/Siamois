package fr.siamois.domain.models.specimen;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;¬¬

@Data
@Entity
@Table(name = "specimen_group")
public class SpecimenGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specimen_group_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

}