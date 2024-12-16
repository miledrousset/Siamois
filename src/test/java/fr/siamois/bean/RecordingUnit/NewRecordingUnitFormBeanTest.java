package fr.siamois.bean.RecordingUnit;

import fr.siamois.models.ActionUnit;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.models.recordingunit.RecordingUnitSize;
import fr.siamois.services.RecordingUnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.time.OffsetDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewRecordingUnitFormBeanTest {

    @Mock
    private RecordingUnitService recordingUnitService;  // Mock the RecordingUnitService

    @InjectMocks
    private NewRecordingUnitFormBean newRecordingUnitFormBean;  // HomeBean under test

    private RecordingUnit recordingUnit;

    @BeforeEach
    void setUp() {
        recordingUnit = new RecordingUnit();
        //recordingUnit.setId(1L);
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
        when(recordingUnitService.findById(1)).thenReturn(recordingUnit);

        // When: call the @PostConstruct method (implicitly triggered during bean initialization)
        newRecordingUnitFormBean.init();

        // Then: verify that the bean is populated properly
        assertEquals(recordingUnit, newRecordingUnitFormBean.getRecordingUnit());
    }
}