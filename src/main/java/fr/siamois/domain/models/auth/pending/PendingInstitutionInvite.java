package fr.siamois.domain.models.auth.pending;

import fr.siamois.domain.models.Institution;
import jakarta.persistence.*;
import jakarta.ws.rs.DefaultValue;
import lombok.Data;

import java.io.Serializable;

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

}
