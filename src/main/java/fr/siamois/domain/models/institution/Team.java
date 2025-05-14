package fr.siamois.domain.models.institution;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "team", schema = "public")
public class Team implements Serializable, Comparable<Team> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_institution_id", nullable = false)
    private Institution institution;

    @DefaultValue("false")
    @Column(name = "is_default_team", nullable = false)
    private boolean defaultTeam = false;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @DefaultValue("NOW()")
    @Column(name = "creation_date", nullable = false)
    private OffsetDateTime creationDate = OffsetDateTime.now();

    @Override
    public int compareTo(Team o) {
        return this.getId().compareTo(o.getId());
    }
}