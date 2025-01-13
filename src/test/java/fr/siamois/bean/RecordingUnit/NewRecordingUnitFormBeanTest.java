package fr.siamois.bean.RecordingUnit;

import fr.siamois.bean.RecordingUnit.utils.RecordingUnitUtils;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.models.recordingunit.RecordingUnitSize;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.RecordingUnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static java.time.OffsetDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewRecordingUnitFormBeanTest {

    @Mock
    private RecordingUnitService recordingUnitService;  // Mock the RecordingUnitService
    @Mock
    private ActionUnitService actionUnitService;
    @Mock
    private RecordingUnitUtils recordingUnitUtils;



    @InjectMocks
    private NewRecordingUnitFormBean newRecordingUnitFormBean;  // HomeBean under test

    private RecordingUnit recordingUnit;
    private ActionUnit actionUnit;

    @BeforeEach
    void setUp() {
        // the action unit the new recording unit will be attached to
        actionUnit = new ActionUnit();


        recordingUnit = new RecordingUnit();
        //recordingUnit.setId(1L);
        this.recordingUnit.setActionUnit(actionUnit);
        this.recordingUnit.setDescription("Nouvelle description");
        //this.startDate = recordingUnitUtils.offsetDateTimeToLocalDate(now());
        // Below is hardcoded but it should not be. TODO
        //ActionUnit actionUnit = this.actionUnitService.findById(4);
        //this.recordingUnit.setActionUnit(actionUnit);
        this.recordingUnit.setSerial_id(1);
        // Init size & altimetry
        this.recordingUnit.setSize(new RecordingUnitSize());
        this.recordingUnit.getSize().setSize_unit("cm");
        this.recordingUnit.setAltitude(new RecordingUnitAltimetry());
        this.recordingUnit.getAltitude().setAltitude_unit("m");




    }


    @Test
    void save() {

    }

    @Test
    void offsetDateTimeToLocalDate() {
    }

    @Test
    void localDateToOffsetDateTime() {
    }

    @Test
    void completePerson() {
    }

    @Test
    void init_success() {
        // Given: mock the services
        when(actionUnitService.findById(4)).thenReturn(actionUnit);
        when(recordingUnitUtils.offsetDateTimeToLocalDate(any(OffsetDateTime.class))).thenReturn(LocalDate.MAX);

        // When: call the @PostConstruct method (implicitly triggered during bean initialization)
        newRecordingUnitFormBean.init();

        // Then: verify that the bean is populated properly
        assertEquals(recordingUnit, newRecordingUnitFormBean.getRecordingUnit());
        // TODO : check other local var
    }
}