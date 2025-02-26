package fr.siamois.domain.services.vocabulary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FieldServiceTest {

    private FieldService fieldService;

    @BeforeEach
    void setUp() {
        fieldService = new FieldService();
    }

    @Test
    void searchAllFieldCodes() {
        List<String> fieldCodes = fieldService.searchAllFieldCodes();

        assertThat(fieldCodes).isNotEmpty();
    }

}