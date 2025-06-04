package fr.siamois.ui.lazydatamodel;

import fr.siamois.domain.models.recordingunit.RecordingUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void isFilterCriteriaSame_fieldNotEqual_String() {

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
    void isFilterCriteriaSame_fieldNotEqual_Object() {

        filterBy2 = new HashMap<>();
        filterBy = new HashMap<>();
        RecordingUnit r = new RecordingUnit();
        RecordingUnit r2 = new RecordingUnit();
        r.setFullIdentifier("f1");
        r2.setFullIdentifier("f2");
        filterBy2.put("f1", FilterMeta.builder().field("f1").filterValue(r).build());
        filterBy.put("f1", FilterMeta.builder().field("f1").filterValue(r2).build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isFilterCriteriaSame(filterBy, filterBy2);
        assertFalse(res);
    }

    @Test
    void isFilterCriteriaSame_fieldNotEqual_Collection() {

        filterBy2 = new HashMap<>();
        filterBy = new HashMap<>();
        RecordingUnit r = new RecordingUnit();
        RecordingUnit r2 = new RecordingUnit();
        RecordingUnit r3 = new RecordingUnit();
        RecordingUnit r4 = new RecordingUnit();
        r.setFullIdentifier("f1");
        r2.setFullIdentifier("f2");
        r3.setFullIdentifier("f1");
        r4.setFullIdentifier("f3");
        List<RecordingUnit> l1 = new ArrayList<>();
        l1.add(r);
        l1.add(r2);
        List<RecordingUnit> l2 = new ArrayList<>();
        l2.add(r3);
        l2.add(r4);
        filterBy2.put("f1", FilterMeta.builder().field("f1").filterValue(l1).build());
        filterBy.put("f1", FilterMeta.builder().field("f1").filterValue(l2).build());

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

    @Test
    void isFilterCriteriaSame_fieldEqual_Object() {

        filterBy2 = new HashMap<>();
        filterBy = new HashMap<>();
        RecordingUnit r = new RecordingUnit();
        RecordingUnit r2 = new RecordingUnit();
        r.setFullIdentifier("f1");
        r2.setFullIdentifier("f1");
        filterBy2.put("f1", FilterMeta.builder().field("f1").filterValue(r).build());
        filterBy.put("f1", FilterMeta.builder().field("f1").filterValue(r2).build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isFilterCriteriaSame(filterBy, filterBy2);
        assertTrue(res);
    }

    @Test
    void isFilterCriteriaSame_fieldEqual_Collection() {

        filterBy2 = new HashMap<>();
        filterBy = new HashMap<>();
        RecordingUnit r = new RecordingUnit();
        RecordingUnit r2 = new RecordingUnit();
        RecordingUnit r3 = new RecordingUnit();
        RecordingUnit r4 = new RecordingUnit();
        r.setFullIdentifier("f1");
        r2.setFullIdentifier("f2");
        r3.setFullIdentifier("f2");
        r4.setFullIdentifier("f1");
        List<RecordingUnit> l1 = new ArrayList<>();
        l1.add(r);
        l1.add(r2);
        List<RecordingUnit> l2 = new ArrayList<>();
        l2.add(r3);
        l2.add(r4);
        filterBy2.put("f1", FilterMeta.builder().field("f1").filterValue(l1).build());
        filterBy.put("f1", FilterMeta.builder().field("f1").filterValue(l2).build());

        BaseLazyDataModel lazyModel = Mockito.mock(BaseLazyDataModel.class, Answers.CALLS_REAL_METHODS);

        // act
        Boolean res = lazyModel.isFilterCriteriaSame(filterBy, filterBy2);
        assertTrue(res);
    }


}