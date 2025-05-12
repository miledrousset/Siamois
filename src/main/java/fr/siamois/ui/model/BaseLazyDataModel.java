package fr.siamois.ui.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Struct;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.util.Map;
import java.util.Set;


@Getter
public abstract class BaseLazyDataModel<T> extends LazyDataModel<T> {

    @Override
    public int count(Map<String, FilterMeta> map) {
        return 0;
    }

    @Setter
    protected Integer first ;
    @Setter
    protected Integer pageSizeState;
    @Setter
    protected transient Set<SortMeta> sortBy ;
    @Setter
    protected transient Map<String, FilterMeta> filterBy ;

    public int getFirstIndexOnPage() {
        return first + 1; // Adding 1 because indexes are zero-based
    }

    public int getLastIndexOnPage() {
        int last = first + pageSizeState;
        int total = this.getRowCount();
        return Math.min(last, total); // Ensure it doesn’t exceed total records
    }
}
