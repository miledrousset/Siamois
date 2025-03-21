package fr.siamois.domain.models.actionunit;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ActionUnitFormMapping {

    @EmbeddedId
    private ActionUnitFormMappingId pk;
}
