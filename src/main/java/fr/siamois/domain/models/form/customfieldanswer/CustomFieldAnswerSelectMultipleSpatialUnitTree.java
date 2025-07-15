package fr.siamois.domain.models.form.customfieldanswer;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import jakarta.persistence.*;
import lombok.Data;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import java.util.List;
import java.util.Objects;


@Data
@Entity
@DiscriminatorValue("SELECT_MULTIPLE_SPATIAL_UNIT_TREE")
@Table(name = "custom_field_answer")
public class CustomFieldAnswerSelectMultipleSpatialUnitTree extends CustomFieldAnswer {

    @Transient
    // Attention : CheckboxTreeNode<SpatialUnit> est très lié à PrimeFaces, à voir pour ajouter un niveau d'abstraction
    private List<CheckboxTreeNode<SpatialUnit>> value;
    private transient TreeNode<SpatialUnit> root;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldAnswerSelectMultipleSpatialUnitTree that)) return false;
        if (!super.equals(o)) return false; // Ensures any inherited fields are compared

        return Objects.equals(getPk(), that.getPk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPk());
    }

}
