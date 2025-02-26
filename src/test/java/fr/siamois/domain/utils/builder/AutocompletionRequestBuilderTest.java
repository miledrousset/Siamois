package fr.siamois.domain.utils.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutocompletionRequestBuilderTest {

    @Test
    void testBuilder_withFullConcept() {
        String server = "https://domaine.exemple";
        String thesoExtId = "th21";
        String input = "S";
        AutocompletionRequestBuilder builder = AutocompletionRequestBuilder.getBuilder(server, thesoExtId, input);

        builder.withLang("fr")
                .withLang("en")
                .withGroup("g123")
                .withFullConcepts()
                .withGroup("g125");

        String result = builder.build();

        assertEquals("https://domaine.exemple/openapi/v1/concept/th21/autocomplete/S?lang=en,fr&group=g123,g125&full=true", result);
    }

    @Test
    void testBuilder_withoutFullConcept() {
        String server = "https://domaine.exemple";
        String thesoExtId = "th21";
        String input = "S";
        AutocompletionRequestBuilder builder = AutocompletionRequestBuilder.getBuilder(server, thesoExtId, input);

        builder.withLang("en")
                .withoutFullConcepts()
                .withGroup("g123");

        String result = builder.build();

        assertEquals("https://domaine.exemple/openapi/v1/concept/th21/autocomplete/S?lang=en&group=g123", result);
    }

}