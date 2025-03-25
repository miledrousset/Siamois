package fr.siamois.domain.services.document.compressor;

import fr.siamois.domain.models.document.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.MimeType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class BrowserDisplayableCompressorTest {

    private BrowserDisplayableCompressor compressor;

    @BeforeEach
    void setUp() {
        compressor = new BrowserDisplayableCompressor();
    }

    @Test
    void testIsMatchingCompressor() {
        assertTrue(compressor.isMatchingCompressor(MimeType.valueOf("image/png")));
        assertTrue(compressor.isMatchingCompressor(MimeType.valueOf("application/pdf")));
        assertFalse(compressor.isMatchingCompressor(MimeType.valueOf("text/plain")));
    }

    @Test
    void testCompress() throws IOException {
        String data = "test data";
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());
        byte[] compressedData = compressor.compress(inputStream);
        assertArrayEquals(data.getBytes(), compressedData);
    }

    @Test
    void testEncodingTypes_assertThrows() {
        assertThrows(UnsupportedOperationException.class, () -> compressor.encodingTypes());
    }

    @Test
    void testUpdateStoredFilename() {
        Document document = new Document();
        document.setFileCode("12345");
        document.setMimeType("application/pdf");
        document.setFileName("test.pdf");

        compressor.updateStoredFilename(document);
        assertEquals("12345.pdf", document.getStoredFileName());
    }
}