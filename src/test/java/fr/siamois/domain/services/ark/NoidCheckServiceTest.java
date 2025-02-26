package fr.siamois.domain.services.ark;

import fr.siamois.domain.services.ark.NoidCheckService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoidCheckServiceTest {

    private final NoidCheckService service = new NoidCheckService();

    private static Stream<Arguments> provideValues() {
        return Stream.of(
                Arguments.of("81RQ5B6Q-N", "b"),
                Arguments.of("76TCKJ6F-H", "h"),
                Arguments.of("VDQK9VRT-V", "j")
        );
    }

    @ParameterizedTest
    @MethodSource("provideValues")
    void getControlCharacter(String idArkWithoutPrefix, String expectedCheckCode) {
        String checkCode = service.calculateCheckDigit(idArkWithoutPrefix.toLowerCase());
        assertEquals(expectedCheckCode, checkCode);
    }
}