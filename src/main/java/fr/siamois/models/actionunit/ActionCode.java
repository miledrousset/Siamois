package fr.siamois.models.actionunit;


import fr.siamois.models.FieldCode;
import fr.siamois.models.vocabulary.Concept;
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

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "fk_type", nullable = false)
    protected Concept type;

    @FieldCode
    public static final String TYPE_FIELD_CODE = "SIAAC.TYPE";
}
