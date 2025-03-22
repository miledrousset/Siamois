package fr.siamois.domain.models.actionunit;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "action_unit_form_mapping")
public class ActionUnitFormMapping {

    @EmbeddedId
    private ActionUnitFormMappingId pk;
}
