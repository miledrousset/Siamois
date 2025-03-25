package fr.siamois.domain.models.actionunit;

import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter

public class ActionUnitFormMappingId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "fk_action_unit", nullable = false)
    private ActionUnit actionUnit;

    @ManyToOne
    @JoinColumn(name = "fk_custom_form", nullable = false)
    private transient CustomForm form;

    @ManyToOne
    @JoinColumn(name = "fk_concept", nullable = false)
    private Concept concept;

    @Column(name= "table_name", nullable = false)
    private String tableName;

}
