package fr.siamois.bean.RecordingUnit;

import fr.siamois.bean.SpatialUnit.SpatialUnitBean;
import fr.siamois.models.auth.Person;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.utils.AuthenticatedUserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordingUnitFormBeanTest {

    @Mock
    private RecordingUnitService recordingUnitService;  // Mock the RecordingUnitService

    @InjectMocks
    private RecordingUnitFormBean recordingUnitFormBean;  // HomeBean under test

    private RecordingUnit recordingUnit;
    private Person authenticatedUser;

    @BeforeEach
    void setUp() {
        recordingUnit = new RecordingUnit();
        recordingUnit.setId(1L);

        authenticatedUser = new Person();

        // Initialize the bean ID
        recordingUnitFormBean.setId(1L);
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

        try (MockedStatic<AuthenticatedUserUtils> utilities = Mockito.mockStatic(AuthenticatedUserUtils.class)) {
            utilities.when(AuthenticatedUserUtils::getAuthenticatedUser).thenReturn(Optional.of(authenticatedUser));
            // When: call the @PostConstruct method (implicitly triggered during bean initialization)
            recordingUnitFormBean.init();
        }



        // Then: verify that the bean is populated properly
        assertEquals(recordingUnit, recordingUnitFormBean.getRecordingUnit());
        // TODO : check other local var
    }
}