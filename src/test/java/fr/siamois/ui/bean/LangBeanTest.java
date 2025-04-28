package fr.siamois.ui.bean;

import fr.siamois.domain.services.LangService;
import fr.siamois.domain.services.person.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LangBeanTest {

    private LangBean langBean;

    @Mock
    private LangService langService;

    @Mock
    private PersonService personService;

    @BeforeEach
    void setUp() {
        langBean = new LangBean(langService, personService);
    }

    @Test
    void getLangs_shouldReturnAllLangsWithQuote() {
        when(langService.getAvailableLanguages()).thenReturn(List.of("fr", "en", "de"));

        List<String> result = langBean.getLangs();

        assertThat(result).containsExactlyInAnyOrder("'fr'", "'en'", "'de'");
    }

}