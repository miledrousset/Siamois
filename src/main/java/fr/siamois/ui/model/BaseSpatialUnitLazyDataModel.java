package fr.siamois.ui.model;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;


public abstract class BaseSpatialUnitLazyDataModel extends BaseLazyDataModel<SpatialUnit> {

    public List<SpatialUnit> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {

        this.first = first;
        this.pageSizeState = pageSize;
        this.sortBy = new HashSet<>(sortBy.values());
        this.filterBy = filterBy;

        FacesContext context = FacesContext.getCurrentInstance();

        int pageNumber = first / pageSizeState;
        Pageable pageable = PageRequest.of(pageNumber, pageSizeState, buildSort(sortBy));

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
                List<ConceptLabel> selectedCategoryLabels = (List<ConceptLabel>) categoryMeta.getFilterValue();
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

        Page<SpatialUnit> result = loadSpatialUnits(nameFilter, categoryIds, personIds, globalFilter, pageable);
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
            else if(Objects.equals(field, "creationTime")) {
                field = "creation_time";
            }
            else if(Objects.equals(field, "author")) {
                field = "p_lastname";
            }
            SortMeta meta = entry.getValue();
            Sort.Order order = new Sort.Order(meta.getOrder() == SortOrder.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC, field);
            orders.add(order);
        }

        return Sort.by(orders);
    }

    protected abstract Page<SpatialUnit> loadSpatialUnits(String nameFilter, Long[] categoryIds, Long[] personIds, String globalFilter, Pageable pageable);

}
