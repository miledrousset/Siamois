package fr.siamois.domain.models.auth.pending;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@Entity
@Table(name = "pending_action_unit_attribution")
@NoArgsConstructor
public class PendingActionUnitAttribution implements Serializable {

    @EmbeddedId
    private PendingActionUnitId id;

    @MapsId("pendingInstitutionInviteId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_pending_institution_invite_id")
    private PendingInstitutionInvite institutionInvite;

    @MapsId("actionUnitId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_action_unit_id")
    private ActionUnit actionUnit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_role_id")
    private Concept role = null;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class PendingActionUnitId implements Serializable {
        private Long pendingInstitutionInviteId;
        private Long actionUnitId;
    }

}
