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
    private transient TreeNode<SpatialUnit> root;
    @Transient // racine "technique" de la TreeTable
    private List<CheckboxTreeNode<SpatialUnit>> selection;   // cases cochées (sélection multiple)
    private final Map<Long, Set<Long>> ancestorCache = new HashMap<>();

    @Transient
    public Set<SpatialUnit> getNormalizedSpatialUnits() {
        return toNormalizedSpatialUnits();
    }

    /** Retourne les USp sélectionnées après normalisation (pas d’enfant si un ancêtre est sélectionné) */
    @Transient
    public Set<SpatialUnit> toNormalizedSpatialUnits() {
        List<CheckboxTreeNode<SpatialUnit>> sel = selection != null ? selection : Collections.emptyList();
        List<CheckboxTreeNode<SpatialUnit>> normalized = normalizeTreeSelection(sel);

        return normalized.stream()
                .map(CheckboxTreeNode::getData)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** Normalise la sélection pour les "chips" au niveau MÉTIER (graph multi-parents). */
    public List<SpatialUnit> getNormalizedSelectedUnits(List<CheckboxTreeNode<SpatialUnit>> selectedNodes) {
        if (selectedNodes == null || selectedNodes.isEmpty()) return Collections.emptyList();

        // 1) Ramène à des IDs uniques d'entités
        Map<Long, SpatialUnit> byId = new HashMap<>();
        Set<Long> selectedIds = new LinkedHashSet<>();
        for (CheckboxTreeNode<SpatialUnit> node : selectedNodes) {
            SpatialUnit u = node.getData();
            if (u == null) continue;
            byId.putIfAbsent(u.getId(), u);
            selectedIds.add(u.getId());
        }

        // 2) Marque les entités "dominées" par un ancêtre sélectionné
        Set<Long> toRemove = new HashSet<>();
        for (Long id : selectedIds) {
            if (toRemove.contains(id)) continue;
            Set<Long> ancestors = getAllAncestorIds(id); // transitif, métier
            // si l'intersection ancestors ∩ selectedIds n'est pas vide -> enlever l'enfant
            for (Long a : ancestors) {
                if (selectedIds.contains(a)) {
                    toRemove.add(id);
                    break;
                }
            }
        }

        // 3) Garde seulement l’ensemble minimal
        selectedIds.removeAll(toRemove);

        // 4) Retourne la liste des entités pour afficher les chips
        List<SpatialUnit> chips = new ArrayList<>(selectedIds.size());
        for (Long id : selectedIds) chips.add(byId.get(id));
        // (optionnel) ordonner
        chips.sort(Comparator.comparing(SpatialUnit::getName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return chips;
    }

    /** Renvoie tous les IDs des ancêtres métier (transitifs), avec détection de cycles. */
    private Set<Long> getAllAncestorIds(long id) {
        // cache simple pour éviter de recalculer
        Set<Long> cached = ancestorCache.get(id);
        if (cached != null) return cached;

        Set<Long> res = new HashSet<>();
        Deque<Long> stack = new ArrayDeque<>(repo.findDirectParentIdsOf(id));
        while (!stack.isEmpty()) {
            long cur = stack.pop();
            if (res.add(cur)) {
                List<Long> parents = repo.findDirectParentIdsOf(cur);
                if (parents != null) {
                    for (Long p : parents) {
                        if (!res.contains(p)) stack.push(p);
                    }
                }
            }
        }
        ancestorCache.put(id, res);
        return res;
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
        this.selection = collectSelectedNodes(root);

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
