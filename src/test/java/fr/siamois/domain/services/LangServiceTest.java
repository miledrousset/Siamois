package fr.siamois.domain.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LangServiceTest {

    @Mock
    private MessageSource messageSource;

    private LangService langService;

    @BeforeEach
    void setUp() {
        langService = new LangService(messageSource);
    }

    @Test
    void msg() {
        String key = "greeting";
        Locale locale = Locale.ENGLISH;
        String expectedMessage = "Hello";

        when(messageSource.getMessage(key, null, locale)).thenReturn(expectedMessage);

        String actualMessage = langService.msg(key, locale);

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testMsg() {
        String format = "greeting";
        Locale locale = Locale.ENGLISH;
        String expectedMessage = "Hello, John!";
        Object[] args = {"John"};

        when(messageSource.getMessage(format, null, locale)).thenReturn("Hello, %s!");

        String actualMessage = langService.msg(format, locale, args);

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void getAvailableLanguages() {
        List<String> languages = langService.getAvailableLanguages();

        assertNotNull(languages);
        assertFalse(languages.isEmpty());
    }

    @Test
    void getDefaultLang() {
        assertNull(langService.getDefaultLang());
    }
}