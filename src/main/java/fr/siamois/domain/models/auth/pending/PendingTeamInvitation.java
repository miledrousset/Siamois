package fr.siamois.domain.models.auth.pending;

import fr.siamois.domain.models.team.Team;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "pending_team_invitation")
public class PendingTeamInvitation implements Serializable {

    @Id
    @Column(name = "pending_team_invitation_id")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PendingInstitutionInvitation pendingInstitutionInvitation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_role_concept_id", nullable = false)
    private Concept roleInTeam;
}
