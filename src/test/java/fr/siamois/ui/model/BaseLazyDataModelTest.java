package fr.siamois.ui.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BaseLazyDataModelTest {

    Map<String, FilterMeta> filterBy;
    Map<String, FilterMeta> filterBy2;
    Map<String, SortMeta> sortBy;
    Map<String, SortMeta> sortBy2;

    @Test
    void isSortCriteriaSame_bothNull() {

        sortBy2 = null;
        sortBy = null;

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isFilterCriteriaSame(sortBy, sortBy2);
        assertTrue(res);
    }

    @Test
    void isSortCriteriaSame_oneNull() {

        sortBy2 = null;
        sortBy = new HashMap<>();

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isSortCriteriaSame(sortBy, sortBy2);
        assertFalse(res);
    }

    @Test
    void isSortCriteriaSame_differentSize() {

        sortBy2 = new HashMap<>();
        sortBy = new HashMap<>();
        sortBy2.put("sort1", SortMeta.builder().field("sort1").build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isSortCriteriaSame(sortBy, sortBy2);
        assertFalse(res);
    }

    @Test
    void isSortCriteriaSame_fieldNotFound() {

        sortBy2 = new HashMap<>();
        sortBy = new HashMap<>();
        sortBy2.put("sort1", SortMeta.builder().field("sort1").order(SortOrder.ASCENDING).build());
        sortBy.put("sort2", SortMeta.builder().field("sort2").order(SortOrder.ASCENDING).build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isSortCriteriaSame(sortBy, sortBy2);
        assertFalse(res);
    }

    @Test
    void isSortCriteriaSame_fieldNotEqual() {

        sortBy2 = new HashMap<>();
        sortBy = new HashMap<>();
        sortBy2.put("sort1", SortMeta.builder().field("sort1").order(SortOrder.ASCENDING).build());
        sortBy.put("sort1", SortMeta.builder().field("sort1").order(SortOrder.DESCENDING).build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isSortCriteriaSame(sortBy, sortBy2);
        assertFalse(res);
    }

    @Test
    void isSortCriteriaSame_fieldEqual() {

        sortBy2 = new HashMap<>();
        sortBy = new HashMap<>();
        sortBy2.put("sort1", SortMeta.builder().field("sort1").order(SortOrder.ASCENDING).build());
        sortBy.put("sort1", SortMeta.builder().field("sort1").order(SortOrder.ASCENDING).build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isSortCriteriaSame(sortBy, sortBy2);
        assertTrue(res);
    }

    @Test
    void isFilterCriteriaSame_bothNull() {

        filterBy2 = null;
        filterBy = null;

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isFilterCriteriaSame(filterBy, filterBy2);
        assertTrue(res);
    }

    @Test
    void isFilterCriteriaSame_oneNull() {

        filterBy2 = null;
        filterBy = new HashMap<>();

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isFilterCriteriaSame(filterBy, filterBy2);
        assertFalse(res);
    }

    @Test
    void isFilterCriteriaSame_differentSize() {

        filterBy2 = new HashMap<>();
        filterBy = new HashMap<>();
        filterBy2.put("f1", FilterMeta.builder().field("f1").build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isFilterCriteriaSame(filterBy, filterBy2);
        assertFalse(res);
    }

    @Test
    void isFilterCriteriaSame_fieldNotFound() {

        filterBy2 = new HashMap<>();
        filterBy = new HashMap<>();
        filterBy2.put("f1", FilterMeta.builder().field("f1").filterValue("test").build());
        filterBy.put("f2", FilterMeta.builder().field("f2").filterValue("test").build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isFilterCriteriaSame(filterBy, filterBy2);
        assertFalse(res);
    }

    @Test
    void isFilterCriteriaSame_fieldNotEqual() {

        filterBy2 = new HashMap<>();
        filterBy = new HashMap<>();
        filterBy2.put("f1", FilterMeta.builder().field("f1").filterValue("test").build());
        filterBy.put("f1", FilterMeta.builder().field("f1").filterValue("test2").build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isFilterCriteriaSame(filterBy, filterBy2);
        assertFalse(res);
    }

    @Test
    void isFilterCriteriaSame_fieldEqual() {

        filterBy2 = new HashMap<>();
        filterBy = new HashMap<>();
        filterBy2.put("f1", FilterMeta.builder().field("f1").filterValue("test").build());
        filterBy.put("f1", FilterMeta.builder().field("f1").filterValue("test").build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isFilterCriteriaSame(filterBy, filterBy2);
        assertTrue(res);
    }


}