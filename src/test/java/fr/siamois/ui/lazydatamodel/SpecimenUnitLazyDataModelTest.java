package fr.siamois.ui.lazydatamodel;

import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.models.vocabulary.Concept;

import fr.siamois.domain.services.specimen.SpecimenService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.event.RowEditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecimenUnitLazyDataModelTest {

    @Mock
    private SpecimenService specimenService;
    @Mock
    private SessionSettingsBean sessionSettingsBean;
    @Mock
    private LangBean langBean;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @InjectMocks
    private SpecimenLazyDataModel lazyModel;

    Page<Specimen> p ;
    Pageable pageable;
    Specimen unit1;
    Specimen unit2;
    Institution institution;
    RecordingUnit ru;


    @BeforeEach
    void setUp() {
        unit1 = new Specimen();
        unit2 = new Specimen();
        ru = new RecordingUnit();
        institution = new Institution();
        institution.setId(1L);
        unit1.setId(1L);
        unit1.setFullIdentifier("sia-2025-1");
        unit2.setId(2L);
        unit1.setFullIdentifier("sia-2025-2");
        p = new PageImpl<>(List.of(unit1, unit2));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void loadActionUnits_Success() {

        lazyModel = new SpecimenLazyDataModel(specimenService,sessionSettingsBean,langBean);

        // Arrange
        when(specimenService.findAllByInstitutionAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                any(Long.class),
                any(String.class),
                any(Long[].class),
                any(String.class),
                any(String.class),
                any(Pageable.class)
        )).thenReturn(p);
        when(sessionSettingsBean.getSelectedInstitution()).thenReturn(institution);
        when(langBean.getLanguageCode()).thenReturn("en");

        // Act
        Page<Specimen> actualResult = lazyModel.loadSpecimens("null",
                new Long[2],new Long[2], "null", pageable);

        // Assert
        // Assert
        assertEquals(unit1, actualResult.getContent().get(0));
        assertEquals(unit2, actualResult.getContent().get(1));
    }

    private Specimen createUnit(long id) {
        Specimen unit = new Specimen();
        unit.setId(id);
        return unit;
    }


    @Test
    void testGetRowKey_Success() {
        Specimen unit = createUnit(123L);
        String key = lazyModel.getRowKey(unit);
        assertEquals("123", key);
    }

    @Test
    void testGetRowKey_NullInput() {
        String key = lazyModel.getRowKey(null);
        assertNull(key);
    }

    @Test
    void testGetRowData_Success() {
        Specimen expectedUnit = createUnit(456L);
        List<Specimen> units = Arrays.asList(
                createUnit(123L),
                expectedUnit,
                createUnit(789L)
        );
        lazyModel.setWrappedData(units);

        Specimen result = lazyModel.getRowData("456");
        assertNotNull(result);
        assertEquals(456L, result.getId());
    }

    @Test
    void testGetRowData_NotFound() {
        List<Specimen> units = Arrays.asList(
                createUnit(100L),
                createUnit(200L)
        );
        lazyModel.setWrappedData(units);

        Specimen result = lazyModel.getRowData("300");
        assertNull(result);
    }

    @Test
    void testHandleRowEdit_successfulSave() {
        Specimen unit = new Specimen();
        unit.setFullIdentifier("S123");

        RowEditEvent<Specimen> event = mock(RowEditEvent.class);
        when(event.getObject()).thenReturn(unit);

        try (MockedStatic<MessageUtils> messageUtilsMock = mockStatic(MessageUtils.class)) {
            // WHEN
            lazyModel.handleRowEdit(event);


            // THEN
            verify(specimenService).save(unit);

            messageUtilsMock.verify(() ->
                    MessageUtils.displayInfoMessage(langBean, "common.entity.recordingUnits.updated", "S123"));
        }
    }

    @Test
    void testHandleRowEdit_failedSave() {
        Specimen unit = new Specimen();
        unit.setFullIdentifier("S123");

        RowEditEvent<Specimen> event = mock(RowEditEvent.class);
        when(event.getObject()).thenReturn(unit);

        doThrow(new FailedRecordingUnitSaveException("")).when(specimenService).save(any());

        try (MockedStatic<MessageUtils> messageUtilsMock = mockStatic(MessageUtils.class)) {
            // WHEN
            lazyModel.handleRowEdit(event);

            // THEN
            verify(specimenService).save(unit);

            messageUtilsMock.verify(() ->
                    MessageUtils.displayErrorMessage(langBean, "common.entity.recordingUnits.updateFailed", "S123"));
        }
    }

    @Test
    void testSaveFieldBulk_updatesTypeAndDisplaysMessage() {
        Specimen r1 = new Specimen();
        r1.setId(1L);
        Specimen r2 = new Specimen();
        r2.setId(2L);

        Concept newType = new Concept();
        lazyModel.setBulkEditTypeValue(newType);
        lazyModel.setSelectedUnits(List.of(r1, r2));

        when(specimenService.bulkUpdateType(anyList(), eq(newType))).thenReturn(2);

        try (MockedStatic<MessageUtils> messageUtilsMock = mockStatic(MessageUtils.class)) {
            lazyModel.saveFieldBulk();

            // Confirm both were updated
            assertSame(newType, r1.getType());
            assertSame(newType, r2.getType());

            verify(specimenService).bulkUpdateType(List.of(1L, 2L), newType);

            messageUtilsMock.verify(() ->
                    MessageUtils.displayInfoMessage(langBean, "common.entity.recordingUnits.bulkUpdated", 2));
        }


    }

    @Test
    void testDuplicateRow_createsCopyAndAddsToModel() {
        // GIVEN
        Specimen original = new Specimen();
        original.setFullIdentifier("S-Original");
        original.setId(1L);
        original.setType(new Concept());

        Specimen copied = new Specimen(original);
        copied.setId(999L);
        copied.setIdentifier(1);

        // Create spy of lazyModel so we can override getWrappedData() and getRowData()
        BaseSpecimenLazyDataModel spyModel = spy(lazyModel);

        // Mock getWrappedData() to return list containing the original
        doReturn(List.of(original)).when(spyModel).getWrappedData();

        // Mock getRowData() to return the original when called with its ID as a string
        doReturn(original).when(spyModel).getRowData();

        // Mock service behavior
        when(specimenService.generateNextIdentifier(any())).thenReturn(1);
        when(specimenService.save(any())).thenReturn(copied);

        // WHEN
        spyModel.duplicateRow();

        // THEN
        verify(specimenService).generateNextIdentifier(any());
        assertEquals(original.getType(), copied.getType());
        verify(specimenService).save(any());

    }








}