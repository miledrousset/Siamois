package fr.siamois.domain.models.form.customfieldanswer;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import jakarta.persistence.*;
import lombok.Data;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import java.util.*;
import java.util.stream.Collectors;


@Data
@Entity
@DiscriminatorValue("SELECT_MULTIPLE_SPATIAL_UNIT_TREE")
@Table(name = "custom_field_answer")
public class CustomFieldAnswerSelectMultipleSpatialUnitTree extends CustomFieldAnswer {

    @Transient
    // Attention : CheckboxTreeNode<SpatialUnit> est très lié à PrimeFaces, à voir pour ajouter un niveau d'abstraction
    private List<CheckboxTreeNode<SpatialUnit>> value;
    private transient TreeNode<SpatialUnit> root;

    @Transient
    public Set<SpatialUnit> getNormalizedSpatialUnits() {
        return toNormalizedSpatialUnits();
    }

    /** Retourne les USp sélectionnées après normalisation (pas d’enfant si un ancêtre est sélectionné) */
    public Set<SpatialUnit> toNormalizedSpatialUnits() {
        List<CheckboxTreeNode<SpatialUnit>> sel = value != null ? value : Collections.emptyList();
        List<CheckboxTreeNode<SpatialUnit>> normalized = normalizeTreeSelection(sel);

        return normalized.stream()
                .map(CheckboxTreeNode::getData)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }



    /** Filtre tout nœud ayant un ancêtre déjà sélectionné */
    private static List<CheckboxTreeNode<SpatialUnit>> normalizeTreeSelection(
            List<CheckboxTreeNode<SpatialUnit>> selectedNodes) {

        if (selectedNodes == null || selectedNodes.isEmpty()) {
            return Collections.emptyList();
        }

        Set<TreeNode<SpatialUnit>> selectedSet = new HashSet<>(selectedNodes);
        List<CheckboxTreeNode<SpatialUnit>> result = new ArrayList<>(selectedNodes.size());

        for (CheckboxTreeNode<SpatialUnit> node : selectedNodes) {
            TreeNode<SpatialUnit> parent = node.getParent();
            boolean hasSelectedAncestor = false;

            while (parent != null) {
                if (selectedSet.contains(parent)) {
                    hasSelectedAncestor = true;
                    break;
                }
                parent = parent.getParent();
            }

            if (!hasSelectedAncestor) {
                result.add(node);
            }
        }
        return result;
    }

    // Remove a spatial unit from the selection
    public boolean removeSpatialUnit(SpatialUnit su) {
        if (su == null || root == null) return false;

        // 1) Décocher le nœud correspondant (et enlever l'état partiel)
        CheckboxTreeNode<SpatialUnit> node = findNodeById(root, su.getId());
        if (node == null) return false;

        node.setSelected(false);
        node.setPartialSelected(false);

        // Tout décocher sous ce nœud
        unselectDescendants(node);

        // 2) Reconstituer la sélection "value" à partir de l'arbre
        this.value = collectSelectedNodes(root);

        return true;
    }

    /* -------- Helpers privés -------- */

    private CheckboxTreeNode<SpatialUnit> findNodeById(TreeNode<SpatialUnit> node, Long targetId) {
        if (node == null || targetId == null) return null;

        SpatialUnit data = node.getData();
        if (data != null && Objects.equals(data.getId(), targetId) && node instanceof CheckboxTreeNode) {
            @SuppressWarnings("unchecked")
            CheckboxTreeNode<SpatialUnit> cb = (CheckboxTreeNode<SpatialUnit>) node;
            return cb;
        }
        for (TreeNode<SpatialUnit> child : node.getChildren()) {
            CheckboxTreeNode<SpatialUnit> found = findNodeById(child, targetId);
            if (found != null) return found;
        }
        return null;
    }

    private List<CheckboxTreeNode<SpatialUnit>> collectSelectedNodes(TreeNode<SpatialUnit> node) {
        List<CheckboxTreeNode<SpatialUnit>> acc = new ArrayList<>();
        collectSelectedNodesRec(node, acc);
        return acc;
    }

    private void collectSelectedNodesRec(TreeNode<SpatialUnit> node, List<CheckboxTreeNode<SpatialUnit>> acc) {
        if (node instanceof CheckboxTreeNode<?> cb && ((CheckboxTreeNode<?>) cb).isSelected()) {
            @SuppressWarnings("unchecked")
            CheckboxTreeNode<SpatialUnit> typed = (CheckboxTreeNode<SpatialUnit>) cb;
            acc.add(typed);
        }
        for (TreeNode<SpatialUnit> child : node.getChildren()) {
            collectSelectedNodesRec(child, acc);
        }
    }

    // Si tu veux propager la désélection sur tous les descendants (optionnel)
    @SuppressWarnings("unchecked")
    private void unselectDescendants(TreeNode<SpatialUnit> node) {
        for (TreeNode<SpatialUnit> child : node.getChildren()) {
            if (child instanceof CheckboxTreeNode<?> cb) {
                ((CheckboxTreeNode<?>) cb).setSelected(false);
                ((CheckboxTreeNode<?>) cb).setPartialSelected(false);
            }
            unselectDescendants(child);
        }
    }


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


    /*
    Function to normalize a spatial unit tree selection.
     */

}
