package fr.siamois.domain.models.actionunit;

import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter

public class ActionUnitFormMappingId {

    @ManyToOne
    @JoinColumn(name = "fk_action_unit", nullable = false)
    private ActionUnit actionUnit;

    @ManyToOne
    @JoinColumn(name = "fk_custom_form", nullable = false)
    private CustomForm form;

    @ManyToOne
    @JoinColumn(name = "fk_concept", nullable = false)
    private Concept concept;

    @Column(name= "table_name")
    private String tableName;

}
