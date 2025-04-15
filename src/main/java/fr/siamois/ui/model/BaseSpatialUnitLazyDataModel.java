package fr.siamois.ui.model;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public abstract class BaseSpatialUnitLazyDataModel extends BaseLazyDataModel<SpatialUnit> {


    public List<SpatialUnit> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        this.first = first;
        this.pageSize = pageSize;

        int pageNumber = first / pageSize;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, buildSort(sortBy));

        String nameFilter = null;
        Long[] categoryIds = null;
        String globalFilter = null;

        if (filterBy != null) {
            FilterMeta nameMeta = filterBy.get("name");
            if (nameMeta != null && nameMeta.getFilterValue() != null) {
                nameFilter = nameMeta.getFilterValue().toString();
            }

            FilterMeta categoryMeta = filterBy.get("category");
            if (categoryMeta != null && categoryMeta.getFilterValue() != null) {
                List<ConceptLabel> selectedCategoryLabels = (List<ConceptLabel>) categoryMeta.getFilterValue();
                List<Concept> selectedCategories;
                selectedCategories = selectedCategoryLabels.stream().map(ConceptLabel::getConcept).toList();
                categoryIds = selectedCategories.stream()
                        .filter(Objects::nonNull)
                        .map(Concept::getId)
                        .filter(Objects::nonNull)
                        .toArray(Long[]::new);
            }

            FilterMeta globalMeta = filterBy.get("globalFilter");
            if (globalMeta != null && globalMeta.getFilterValue() != null) {
                globalFilter = globalMeta.getFilterValue().toString();
            }
        }

        Page<SpatialUnit> result = loadSpatialUnits(nameFilter, categoryIds, globalFilter, pageable);
        setRowCount((int) result.getTotalElements());
        return result.getContent();
    }

    private Sort buildSort(Map<String, SortMeta> sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (Map.Entry<String, SortMeta> entry : sortBy.entrySet()) {
            String field = entry.getKey();
            if(Objects.equals(field, "category.label")) {
                field = "c_label";
            }
            SortMeta meta = entry.getValue();
            Sort.Order order = new Sort.Order(meta.getOrder() == SortOrder.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC, field);
            orders.add(order);
        }

        return Sort.by(orders);
    }

    protected abstract Page<SpatialUnit> loadSpatialUnits(String nameFilter, Long[] categoryIds, String globalFilter, Pageable pageable);

}
