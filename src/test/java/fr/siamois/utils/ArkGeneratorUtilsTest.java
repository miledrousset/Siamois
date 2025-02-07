package fr.siamois.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArkGeneratorUtilsTest {

    @Test
    void generateArk_shouldGenerateRadomFakeArk_withNaan666666() {
        String result = ArkGeneratorUtils.generateArk();
        assertThat(result).startsWith("666666/");
    }
}