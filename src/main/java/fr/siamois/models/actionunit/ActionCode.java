package fr.siamois.models.actionunit;


import fr.siamois.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "action_code")
public class ActionCode {

    @Id
    @Column(name = "action_code_id", nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "fk_type", nullable = true)
    protected Concept type;





}
