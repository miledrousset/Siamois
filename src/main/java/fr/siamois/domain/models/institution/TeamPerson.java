package fr.siamois.domain.models.institution;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "team_person")
@NoArgsConstructor
public class TeamPerson {

    @EmbeddedId
    private TeamPersonId id;

    @MapsId("fkTeamId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_team_id", nullable = false)
    private Team team;

    @MapsId("fkPersonId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_role_id")
    private Concept roleInTeam;

    @DefaultValue("NOW()")
    @Column(name = "add_date")
    private OffsetDateTime addDate = OffsetDateTime.now();

    public TeamPerson(Team team, Person person, Concept role) {
        this.id = new TeamPersonId(team, person);
        this.team = team;
        this.person = person;
        this.addDate = OffsetDateTime.now();
        this.roleInTeam = role;
    }

    @NoArgsConstructor
    @Embeddable
    public static class TeamPersonId implements Serializable {

        @NotNull
        @Column(name = "fk_team_id", nullable = false)
        private Long fkTeamId;

        @NotNull
        @Column(name = "fk_person_id", nullable = false)
        private Long fkPersonId;

        public TeamPersonId(@NotNull Team team, @NotNull Person person) {
            this.fkTeamId = team.getId();
            this.fkPersonId = person.getId();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
            TeamPersonId entity = (TeamPersonId) o;
            return Objects.equals(this.fkPersonId, entity.fkPersonId) &&
                    Objects.equals(this.fkTeamId, entity.fkTeamId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fkPersonId, fkTeamId);
        }

    }

}