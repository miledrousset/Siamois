package fr.siamois.domain.models.auth.pending;

import fr.siamois.domain.models.institution.Institution;
import jakarta.persistence.*;
import jakarta.ws.rs.DefaultValue;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name = "pending_institution_invitation")
public class PendingInstitutionInvite implements Serializable {

    @Id
    @Column(name = "pending_institution_invitation_id")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_institution_id", nullable = false)
    private Institution institution;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_pending_person_id", nullable = false)
    private PendingPerson pendingPerson;

    @DefaultValue("false")
    @Column(name = "is_manager")
    private boolean manager = false;

    @DefaultValue("false")
    @Column(name = "is_action_manager")
    private boolean actionManager = false;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PendingInstitutionInvite that)) return false;
        return Objects.equals(institution, that.institution) && Objects.equals(pendingPerson, that.pendingPerson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(institution, pendingPerson);
    }
}
