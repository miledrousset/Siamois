package fr.siamois.domain.models.actionunit;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "action_unit_form_mapping")
public class ActionUnitFormMapping {

    @EmbeddedId
    private ActionUnitFormMappingId pk;

    @ManyToOne
    @MapsId("actionUnit")  // Explicitly map the ID field
    @JoinColumn(name = "fk_action_unit", insertable = false, updatable = false)
    private ActionUnit actionUnit;
}
