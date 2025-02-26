package fr.siamois.domain.models.actionunit;


import fr.siamois.domain.models.FieldCode;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "action_code")
public class ActionCode implements Serializable {

    @Id
    @Column(name = "action_code_id", nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_type", nullable = false)
    protected Concept type;

    @FieldCode
    public static final String TYPE_FIELD_CODE = "SIAAC.TYPE";
}
