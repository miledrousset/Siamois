package fr.siamois.ui.model;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
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

import javax.swing.*;
import java.util.*;


public abstract class BaseActionUnitLazyDataModel extends BaseLazyDataModel<ActionUnit> {

    public List<ActionUnit> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {

        // Deep comparison of sort criteria
        boolean isSortSame = isSortCriteriaSame(this.cachedSortBy, sortBy);

        // Deep comparison of filter criteria
        boolean isFilterSame = isFilterCriteriaSame(this.cachedFilterBy, filterBy);

        // Check if current parameters match saved ones and queryResult is not null
        if (this.cachedFirst == first &&
                this.cachedPageSize == pageSize &&
                isSortSame &&
                isFilterSame &&
                this.queryResult != null) {
            setRowCount(this.cachedRowCount);
            return this.queryResult; // Return cached result
        }

        // Update for paginator
        this.first = first;
        this.pageSizeState = pageSize;
        int pageNumber = first / pageSize;
        Pageable pageable = PageRequest.of(pageNumber, pageSizeState, buildSort(sortBy, "spatial_unit_id"));

        String nameFilter = null;
        Long[] categoryIds = null;
        Long[] personIds = null;
        String globalFilter = null;

        if (filterBy != null) {
            FilterMeta nameMeta = filterBy.get("name");
            if (nameMeta != null && nameMeta.getFilterValue() != null) {
                nameFilter = nameMeta.getFilterValue().toString();
            }

            FilterMeta categoryMeta = filterBy.get("category");
            if (categoryMeta != null && categoryMeta.getFilterValue() != null) {
                List<ConceptLabel> selectedCategoryLabels = (List<ConceptLabel>)  categoryMeta.getFilterValue();
                List<Concept> selectedCategories;
                selectedCategories = selectedCategoryLabels.stream().map(ConceptLabel::getConcept).toList();
                categoryIds = selectedCategories.stream()
                        .filter(Objects::nonNull)
                        .map(Concept::getId)
                        .filter(Objects::nonNull)
                        .toArray(Long[]::new);
            }

            FilterMeta personMeta = filterBy.get("author");
            if (personMeta != null && personMeta.getFilterValue() != null) {
                List<Person> selectedPerson;
                selectedPerson = (List<Person>) personMeta.getFilterValue();
                personIds = selectedPerson.stream()
                        .filter(Objects::nonNull)
                        .map(Person::getId)
                        .filter(Objects::nonNull)
                        .toArray(Long[]::new);
            }

            FilterMeta globalMeta = filterBy.get("globalFilter");
            if (globalMeta != null && globalMeta.getFilterValue() != null) {
                globalFilter = globalMeta.getFilterValue().toString();
            }
        }

        // Perform query to DB
        Page<ActionUnit> result = loadActionUnits(pageable);
        setRowCount((int) result.getTotalElements());

        // Update cache
        this.queryResult = result.getContent();
        this.cachedFilterBy = BaseLazyDataModel.deepCopyFilterMetaMap(filterBy);
        this.cachedSortBy = new HashMap<>(sortBy);
        this.cachedFirst = first;
        this.cachedPageSize = pageSize;
        this.cachedRowCount = (int) result.getTotalElements();

        // Sync sortBy
        this.sortBy = new HashSet<>(sortBy.values());

        return result.getContent();
    }

    private Sort buildSort(Map<String, SortMeta> sortBy, String tieBreaker) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (Map.Entry<String, SortMeta> entry : sortBy.entrySet()) {
            // These lines of code could be replaced with a map
            String field = entry.getKey();
            if (Objects.equals(field, "category")) {
                field = "c_label";
            } else if (Objects.equals(field, "creationTime")) {
                field = "creation_time";
            } else if (Objects.equals(field, "author")) {
                field = "p_lastname";
            }
            SortMeta meta = entry.getValue();
            Sort.Order order = new Sort.Order(meta.getOrder() == SortOrder.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC, field);
            orders.add(order);
        }

        // Add tie breaker to make it deterministic
        orders.add(new Sort.Order(Sort.Direction.ASC, tieBreaker));

        return Sort.by(orders);
    }

    protected abstract Page<ActionUnit> loadActionUnits(Pageable pageable);

}
