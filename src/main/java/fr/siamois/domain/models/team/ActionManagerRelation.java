package fr.siamois.domain.models.team;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "action_manager")
@Data
@NoArgsConstructor
public class ActionManagerRelation {

    @EmbeddedId
    private ActionManagerId id;

    @MapsId("institutionId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_institution_id", nullable = false)
    private Institution institution;

    @MapsId("personId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_person_id", nullable = false)
    private Person person;

    public ActionManagerRelation(Institution institution, Person person) {
        this.id = new ActionManagerId(institution, person);
        this.institution = institution;
        this.person = person;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ActionManagerId implements Serializable {
        private Long institutionId;
        private Long personId;

        public ActionManagerId(Institution institution, Person manager) {
            this.institutionId = institution.getId();
            this.personId = manager.getId();
        }

    }

}
