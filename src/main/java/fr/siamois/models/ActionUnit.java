package fr.siamois.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "action_unit")
public class ActionUnit extends ActionUnitParent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_unit_id", nullable = false)
    private Long id;

    @FieldCode
    public static final String TYPE_FIELD_CODE = "SIAAU.TYPE";

}