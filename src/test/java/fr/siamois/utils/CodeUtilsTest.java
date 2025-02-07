package fr.siamois.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeUtilsTest {

    @Test
    void generateCode_shouldGenerateCode_ofLength6() {
        String result = CodeUtils.generateCode(6);
        assertThat(result).hasSize(6);
    }
}