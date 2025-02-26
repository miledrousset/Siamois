package fr.siamois.services;

import fr.siamois.models.Institution;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.settings.InstitutionSettings;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.services.ark.ArkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArkManagerServiceTest {

    @Mock
    private InstitutionService institutionService;

    @Mock
    private ArkService arkService;

    @Mock
    private ArkEntityService arkEntityService1;

    @Mock
    private ArkEntityService arkEntityService2;

    private ArkManagerService arkManagerService;

    @BeforeEach
    void beforeEach() {
        arkManagerService = new ArkManagerService(List.of(arkEntityService1, arkEntityService2), institutionService, arkService);
    }

    @Test
    void addArkToEntitiesWithoutArk() {
        Institution institution = new Institution();
        InstitutionSettings settings = new InstitutionSettings();
        Ark ark = new Ark();
        SpatialUnit s1 = spy(new SpatialUnit());
        SpatialUnit s2 = spy(new SpatialUnit());

        when(institutionService.createOrGetSettingsOf(institution)).thenReturn(settings);
        when(arkService.generateAndSave(settings)).thenReturn(ark);
        doReturn(List.of(s1)).when(arkEntityService1).findWithoutArk(any(Institution.class));
        doReturn(List.of(s2)).when(arkEntityService2).findWithoutArk(any(Institution.class));

        arkManagerService.addArkToEntitiesWithoutArk(institution);

        verify(s1).setArk(ark);
        verify(arkEntityService1).save(s1);
        verify(s2).setArk(ark);
        verify(arkEntityService2).save(s2);
    }
}