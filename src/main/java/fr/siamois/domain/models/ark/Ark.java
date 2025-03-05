package fr.siamois.domain.models.ark;

import fr.siamois.domain.models.Institution;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "ark", schema = "public")
public class Ark implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_id", nullable = false)
    private Long internalId;

    @ManyToOne
    @JoinColumn(name = "fk_institution_id", nullable = false)
    private Institution creatingInstitution;

    @Column(name = "qualifier", nullable = false)
    private String qualifier;

    public String getArkId() {
        String finalQualifier = qualifier;
        if (creatingInstitution.getSettings().getArkIsUppercase()) {
            finalQualifier = finalQualifier.toUpperCase();
        }
        return String.format("ark:/%s/%s", creatingInstitution.getSettings().getArkNaan(), finalQualifier);
    }

}