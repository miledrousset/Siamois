package fr.siamois.ui.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.util.*;


@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseLazyDataModel<T> extends LazyDataModel<T> {

    @Override
    public int count(Map<String, FilterMeta> map) {
        return 0;
    }

    // Page, Sort and Filter state
    protected int first = 0;
    protected int pageSizeState = 10;
    protected transient Set<SortMeta> sortBy = new HashSet<>();

    // Cache
    protected transient Map<String, FilterMeta> cachedFilterBy = new HashMap<>() ;
    protected int cachedFirst ;
    protected int cachedPageSize ;
    protected transient Map<String, SortMeta> cachedSortBy = new HashMap<>() ;
    protected transient List<T> queryResult ; // cache for the result of the query
    protected int cachedRowCount;

    public static Map<String, FilterMeta> deepCopyFilterMetaMap(Map<String, FilterMeta> originalMap) {
        Map<String, FilterMeta> copiedMap = new HashMap<>();
        for (Map.Entry<String, FilterMeta> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            FilterMeta originalMeta = entry.getValue();

            FilterMeta copiedMeta = FilterMeta.builder()
                    .field(originalMeta.getField())
                    .filterValue(originalMeta.getFilterValue())
                    .matchMode(originalMeta.getMatchMode())
                    .build();

            copiedMap.put(key, copiedMeta);
        }
        return copiedMap;
    }

    // Deep comparison method for sort criteria
    public boolean isSortCriteriaSame(Map<String, SortMeta> existingSorts, Map<String, SortMeta> newSorts) {



        if (existingSorts == null && newSorts == null) return true;
        if (existingSorts == null || newSorts == null) return false;

        if (existingSorts.size() != newSorts.size()) return false;



        for (Map.Entry<String, SortMeta> existingEntry : existingSorts.entrySet()) {
            SortMeta newSortMeta = newSorts.get(existingEntry.getKey());
            if (newSortMeta == null) return false;

            // Compare filter metadata details
            if (!areSortMetaOrderEqual(existingEntry.getValue(), newSortMeta)) {
                return false;
            }
        }
        return true;
    }

    // Deep comparison method for filter criteria
    public boolean isFilterCriteriaSame(Map<String, FilterMeta> existingFilters, Map<String, FilterMeta> newFilters) {
        if (existingFilters == null && newFilters == null) return true;
        if (existingFilters == null || newFilters == null) return false;

        if (existingFilters.size() != newFilters.size()) return false;

        for (Map.Entry<String, FilterMeta> existingEntry : existingFilters.entrySet()) {
            FilterMeta newFilterMeta = newFilters.get(existingEntry.getKey());
            if (newFilterMeta == null) return false;

            // Compare filter metadata details
            if (!areFilterMetaValueEqual(existingEntry.getValue(), newFilterMeta)) {
                return false;
            }
        }
        return true;
    }

    // Helper method to compare SortMeta objects
    private boolean areSortMetaOrderEqual(SortMeta sort1, SortMeta sort2) {
        return (sort1.getOrder() == sort2.getOrder());
    }

    // Helper method to compare FilterMeta objects
    private boolean areFilterMetaValueEqual(FilterMeta filter1, FilterMeta filter2) {
        Object value1 = filter1.getFilterValue();
        Object value2 = filter2.getFilterValue();

        if (value1 instanceof Collection && value2 instanceof Collection) {
            Collection<?> col1 = (Collection<?>) value1;
            Collection<?> col2 = (Collection<?>) value2;

            // Compare as sets to ignore order and duplicates
            return new HashSet<>(col1).equals(new HashSet<>(col2));
        }

        // Fallback to standard equality
        return Objects.equals(value1, value2);
    }


    public int getFirstIndexOnPage() {
        return first + 1; // Adding 1 because indexes are zero-based
    }

    public int getLastIndexOnPage() {
        int last = first + pageSizeState;
        int total = this.getRowCount();
        return Math.min(last, total); // Ensure it doesn’t exceed total records
    }
}
