package fr.siamois.domain.services.spatialunit;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.ui.bean.SessionSettingsBean;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SpatialUnitTreeService {

    private final SpatialUnitService spatialUnitService;
    private final SessionSettingsBean sessionSettingsBean;

    public SpatialUnitTreeService(SpatialUnitService spatialUnitService, SessionSettingsBean sessionSettingsBean) {
        this.spatialUnitService = spatialUnitService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public TreeNode<SpatialUnit> buildTree() {

        Map<SpatialUnit, List<SpatialUnit>> neighborMap =
                spatialUnitService.neighborMapOfAllSpatialUnit(sessionSettingsBean.getSelectedInstitution());

        TreeNode<SpatialUnit> rootNode = new CheckboxTreeNode<>(new SpatialUnit(), null);
        rootNode.setRowKey("root"); // rowKey explicite pour la racine

        Map<SpatialUnit, TreeNode<SpatialUnit>> nodes = new HashMap<>();

        for (Map.Entry<SpatialUnit, List<SpatialUnit>> entry : neighborMap.entrySet()) {
            SpatialUnit parentSu = entry.getKey();
            TreeNode<SpatialUnit> parent = nodes.computeIfAbsent(parentSu,
                    k -> createNodeWithKey(parentSu, null));

            for (SpatialUnit child : entry.getValue()) {
                TreeNode<SpatialUnit> childNode = nodes.computeIfAbsent(child,
                        k -> createNodeWithKey(child, parent));
                parent.getChildren().add(childNode);
            }
        }

        for (SpatialUnit spatialUnit : neighborMap.keySet()) {
            if (numberOfParentsOf(neighborMap, spatialUnit) == 0) {
                rootNode.getChildren().add(nodes.get(spatialUnit));
            }
        }

        return rootNode;
    }

    private CheckboxTreeNode<SpatialUnit> createNodeWithKey(SpatialUnit su, TreeNode<SpatialUnit> parent) {
        CheckboxTreeNode<SpatialUnit> node = new CheckboxTreeNode<>(su, parent);
        if (su.getId() != null) {
            node.setRowKey(""+su.getId()); // clé stable
        } else {
            node.setRowKey(UUID.randomUUID().toString()); // fallback si pas d’ID
        }
        return node;
    }

    private long numberOfParentsOf(Map<SpatialUnit, List<SpatialUnit>> neighborMap, SpatialUnit spatialUnit) {
        long count = 0;
        for (Map.Entry<SpatialUnit, List<SpatialUnit>> entry : neighborMap.entrySet()) {
            for (SpatialUnit child : entry.getValue()) {
                if (child.getId().equals(spatialUnit.getId()))
                    count++;
            }
        }
        return count;
    }
}
