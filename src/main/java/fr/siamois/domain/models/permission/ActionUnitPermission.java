package fr.siamois.domain.models.permission;

import fr.siamois.domain.models.actionunit.ActionUnit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ActionUnitPermission extends EntityPermission {

    @JoinColumn(name = "fk_action_unit_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ActionUnit actionUnit;

}
