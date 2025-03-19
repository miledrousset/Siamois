package fr.siamois.domain.services.document.compressor;

import fr.siamois.domain.models.document.Document;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterInputStream;

@Service
@Order
public class OtherCompressor implements FileCompressor {

    @Override
    public boolean isMatchingCompressor(MimeType mimeType) {
        return true;
    }

    /**
     * Use GZIP compression for any other file
     * @param inputStream The file input stream
     * @return The compressed byte array
     */
    @Override
    public byte[] compress(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DeflaterInputStream deflaterInputStream = new DeflaterInputStream(inputStream)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = deflaterInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public String encodingTypes() {
        return "gzip,deflate";
    }

    @Override
    public void updateStoredFilename(Document document) {
        document.setStoredFileName(document.getFileCode() + ".gzip");
    }

}
