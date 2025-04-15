package fr.siamois.ui.model;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;

import java.util.Map;

public abstract class BaseLazyDataModel<T> extends LazyDataModel<T> {

    @Override
    public int count(Map<String, FilterMeta> map) {
        return 0;
    }

    protected Integer first ;
    protected Integer pageSize ;

    public int getFirstIndexOnPage() {
        return first + 1; // Adding 1 because indexes are zero-based
    }

    public int getLastIndexOnPage() {
        int last = first + pageSize;
        int total = this.getRowCount();
        return Math.min(last, total); // Ensure it doesn’t exceed total records
    }
}
