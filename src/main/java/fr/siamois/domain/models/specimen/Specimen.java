package fr.siamois.domain.models.specimen;


import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.FieldCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "specimen")
public class Specimen extends SpecimenParent implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specimen_id", nullable = false)
    private Long id;

    @FieldCode
    public static final String CATEGORY_FIELD = "SIAS.CATEGORY";

    @FieldCode
    public static final String METHOD_FIELD = "SIAS.METHOD";

}