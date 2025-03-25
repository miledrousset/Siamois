package fr.siamois.domain.services.form;

import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.infrastructure.database.repositories.form.FormRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormServiceTest {

    @Mock
    private FormRepository formRepository;

    @InjectMocks
    private FormService formService;

    @Test
    void findAllFieldsBySpatialUnitId_success() {

        when(formRepository.findById(anyLong()))
                .thenReturn(Optional.of(new CustomForm()));

        // act
        CustomForm res = formService.findById(anyLong());

        assertNotNull(res);
        assertInstanceOf(CustomForm.class, res);

    }

    @Test
    void findAllFieldsBySpatialUnitId_null() {

        when(formRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        // act
        CustomForm res = formService.findById(anyLong());
        assertNull(res);

    }
}