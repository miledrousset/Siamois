package fr.siamois.domain.models.team;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "team_member")
@Data
@NoArgsConstructor
public class TeamMemberRelation {

    @EmbeddedId
    private TeamMemberId id;

    @MapsId("actionUnitId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_action_unit_id", nullable = false)
    private ActionUnit actionUnit;

    @MapsId("personId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_role_id")
    private Concept role = null;

    public TeamMemberRelation(ActionUnit actionUnit, Person person) {
        this.id = new TeamMemberId(actionUnit.getId(), person.getId());
        this.actionUnit = actionUnit;
        this.person = person;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TeamMemberId implements Serializable {
        private Long actionUnitId;
        private Long personId;

        public TeamMemberId(Long actionUnitId, Long personId) {
            this.actionUnitId = actionUnitId;
            this.personId = personId;
        }

    }

}
