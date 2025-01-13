package fr.siamois.bean.RecordingUnit;

import fr.siamois.bean.SpatialUnit.SpatialUnitBean;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.utils.AuthenticatedUserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordingUnitFormBeanTest {

    @Mock
    private RecordingUnitService recordingUnitService;  // Mock the RecordingUnitService
    @Mock
    private FieldConfigurationService fieldConfigurationService;

    @InjectMocks
    private RecordingUnitFormBean recordingUnitFormBean;  // HomeBean under test

    private RecordingUnit recordingUnit;
    private Person authenticatedUser;
    private Vocabulary vocabulary;
    private VocabularyCollection vocabularyCollection;
    private FieldConfigurationWrapper fieldConfigWrapper;

    @BeforeEach
    void setUp() {
        recordingUnit = new RecordingUnit();
        recordingUnit.setId(1L);

        vocabulary = new Vocabulary();
        vocabularyCollection = new VocabularyCollection();
        fieldConfigWrapper = new FieldConfigurationWrapper(vocabulary, List.of(vocabularyCollection));

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
    void init_success() throws NoConfigForField {
        // Given: mock the services
        when(recordingUnitService.findById(1)).thenReturn(recordingUnit);
        when(fieldConfigurationService.fetchConfigurationOfFieldCode(authenticatedUser,RecordingUnit.TYPE_FIELD_CODE)).thenReturn(fieldConfigWrapper);

        try (MockedStatic<AuthenticatedUserUtils> utilities = Mockito.mockStatic(AuthenticatedUserUtils.class)) {
            utilities.when(AuthenticatedUserUtils::getAuthenticatedUser).thenReturn(Optional.of(authenticatedUser));
            // When: call the @PostConstruct method (implicitly triggered during bean initialization)
            recordingUnitFormBean.init();
            // Then: verify that the bean is populated properly
            assertEquals(recordingUnit, recordingUnitFormBean.getRecordingUnit());
        }




        // TODO : check other local var
    }
}