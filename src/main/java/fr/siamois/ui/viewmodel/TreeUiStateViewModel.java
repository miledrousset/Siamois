package fr.siamois.ui.viewmodel;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import lombok.Data;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TreeUiStateViewModel implements Serializable {
    private transient TreeNode<SpatialUnit> root;
    private List<CheckboxTreeNode<SpatialUnit>> selection = new ArrayList<>();
    private Map<Long, CheckboxTreeNode<SpatialUnit>> index = new HashMap<>();
    // getters/setters
}