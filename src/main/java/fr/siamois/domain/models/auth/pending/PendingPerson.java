package fr.siamois.domain.models.auth.pending;

import fr.siamois.domain.models.Institution;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "pending_person")
@Data
public class PendingPerson implements Serializable {

    @Id
    @Column(name = "pending_person_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "fk_institution_id", nullable = false)
    private Institution institution;

    @Column(name = "register_token")
    private String registerToken;

    @Column(name = "register_token_expiration_date")
    private OffsetDateTime pendingInvitationExpirationDate;

}
