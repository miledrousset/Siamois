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

    // Deep comparison method for sort criteria
    public boolean isSortCriteriaSame(Map<String, SortMeta> existingSorts, Map<String, SortMeta> newSorts) {



        if (existingSorts == null && newSorts == null) return true;
        if (existingSorts == null || newSorts == null) return false;

        if (existingSorts.size() != newSorts.size()) return false;



        for (Map.Entry<String, SortMeta> existingEntry : existingSorts.entrySet()) {
            SortMeta newSortMeta = newSorts.get(existingEntry.getKey());
            if (newSortMeta == null) return false;

            // Compare filter metadata details
            if (!areSortMetaEqual(existingEntry.getValue(), newSortMeta)) {
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
            if (!areFilterMetaEqual(existingEntry.getValue(), newFilterMeta)) {
                return false;
            }
        }
        return true;
    }

    // Helper method to compare SortMeta objects
    private boolean areSortMetaEqual(SortMeta sort1, SortMeta sort2) {
        return (sort1.equals(sort2) && (sort1.getOrder() == sort2.getOrder()));
    }

    // Helper method to compare FilterMeta objects
    private boolean areFilterMetaEqual(FilterMeta filter1, FilterMeta filter2) {

        return (filter1.equals(filter2) && (filter1.getFilterValue() == filter2.getFilterValue()));
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
