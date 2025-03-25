package fr.siamois.domain.services.document.compressor;

import fr.siamois.domain.models.document.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.MimeType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class OtherCompressorTest {

    private OtherCompressor compressor;

    @BeforeEach
    void setUp() {
        compressor = new OtherCompressor();
    }

    @Test
    void testIsMatchingCompressor() {
        assertTrue(compressor.isMatchingCompressor(MimeType.valueOf("application/json")));
        assertTrue(compressor.isMatchingCompressor(MimeType.valueOf("text/plain")));
    }

    @Test
    void testCompress() throws IOException {
        String data = "test data";
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());
        byte[] compressedData = compressor.compress(inputStream);
        assertNotNull(compressedData);
        assertTrue(compressedData.length > 0);
    }

    @Test
    void testEncodingTypes() {
        assertEquals("gzip", compressor.encodingTypes());
    }

    @Test
    void testUpdateStoredFilename() {
        Document document = new Document();
        document.setFileCode("12345");
        document.setMimeType("application/json");
        document.setFileName("test.json");

        compressor.updateStoredFilename(document);
        assertEquals("12345.gzip", document.getStoredFileName());
    }
}