package fr.siamois.models;

import fr.siamois.models.auth.Person;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "institution")
public class Institution implements Serializable {
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
    @Column(name = "identifier", nullable = false, length = Integer.MAX_VALUE)
    private String identifier;

    @Column(name = "ark_naan")
    private String arkNaan;

    @Column(name = "ark_prefix")
    private String arkPrefix;

    @Column(name = "ark_size")
    private Integer arkSize;

    @DefaultValue("FALSE")
    @Column(name = "ark_is_uppercase")
    private Boolean arkIsUppercase;

}