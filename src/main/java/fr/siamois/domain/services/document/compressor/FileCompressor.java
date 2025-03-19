package fr.siamois.domain.services.document.compressor;

import fr.siamois.domain.models.document.Document;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.io.InputStream;

public interface FileCompressor {
    boolean isMatchingCompressor(MimeType mimeType);
    byte[] compress(InputStream inputStream) throws IOException;
    String encodingTypes();
    void updateStoredFilename(Document document);
}
