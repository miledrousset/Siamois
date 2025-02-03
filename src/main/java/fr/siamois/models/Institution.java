package fr.siamois.models;

import fr.siamois.models.auth.Person;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "institution")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institution_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "institution_name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "institution_description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_manager_id", nullable = false)
    private Person manager;

    @NotNull
    @Column(name = "institution_code", nullable = false, length = Integer.MAX_VALUE)
    private String code;

}