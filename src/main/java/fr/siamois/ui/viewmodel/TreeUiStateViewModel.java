package fr.siamois.ui.viewmodel;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import lombok.Data;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class TreeUiStateViewModel implements Serializable {
    private transient TreeNode<SpatialUnit> root;
    private Set<SpatialUnit> selection = new HashSet<>();
    private Map<Long, CheckboxTreeNode<SpatialUnit>> index = new HashMap<>();
    // getters/setters
}