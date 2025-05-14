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


    public int getFirstIndexOnPage() {
        return first + 1; // Adding 1 because indexes are zero-based
    }

    public int getLastIndexOnPage() {
        int last = first + pageSizeState;
        int total = this.getRowCount();
        return Math.min(last, total); // Ensure it doesn’t exceed total records
    }
}
