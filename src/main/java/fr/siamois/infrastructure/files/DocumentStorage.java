package fr.siamois.infrastructure.files;

import fr.siamois.domain.models.document.Document;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DocumentStorage {

    @Value("${siamois.documents.allowed-types}")
    private String[] mimeTypes;

    @Getter
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxUploadSize;

    @Value("${siamois.documents.folder-path}")
    private String documentsPath;


    public List<MimeType> supportedMimeTypes() {
        List<MimeType> types = new ArrayList<>();
        for (String mimeType : mimeTypes) {
            types.add(MimeType.valueOf(mimeType));
        }
        return types;
    }

    public void save(Path path, InputStream inputStream) throws IOException {
        Files.createDirectories(path.getParent());

        File file = path.toFile();
        if (file.createNewFile()) {
            log.info("File created: {}", file.getAbsolutePath());
        }
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public Optional<File> find(Document document) {
        Path filePath = Paths.get(
                documentsPath,
                document.getCreatedByInstitution().getId().toString(),
                document.getAuthor().getId().toString(),
                document.storedFileName()
        );

        if (filePath.toFile().exists()) {
            return Optional.of(filePath.toFile());
        }

        return Optional.empty();
    }

}
