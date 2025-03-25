package fr.siamois.domain.services.document.compressor;

import fr.siamois.domain.models.document.Document;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.io.InputStream;

@Service
@Order(1)
public class BrowserDisplayableCompressor implements FileCompressor {

    @Override
    public boolean isMatchingCompressor(MimeType mimeType) {
        return mimeType.getType().equalsIgnoreCase("image")
                || mimeType.toString().equalsIgnoreCase("application/pdf");
    }

    @Override
    public byte[] compress(InputStream inputStream) throws IOException {
        return inputStream.readAllBytes();
    }

    @Override
    public String encodingTypes() {
        throw new UnsupportedOperationException("The file is not compressed. So there aren't any encoding types.");
    }

    @Override
    public void updateStoredFilename(Document document) {
        document.setStoredFileName(document.getFileCode() + "." + document.fileExtension());
    }


}
