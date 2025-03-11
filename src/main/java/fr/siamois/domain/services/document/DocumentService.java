package fr.siamois.domain.services.document;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.document.DocumentParent;
import fr.siamois.domain.models.exceptions.InvalidFileSizeException;
import fr.siamois.domain.models.exceptions.InvalidFileTypeException;
import fr.siamois.domain.services.ArkEntityService;
import fr.siamois.domain.utils.CodeUtils;
import fr.siamois.domain.utils.DocumentUtils;
import fr.siamois.infrastructure.repositories.DocumentRepository;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class DocumentService implements ArkEntityService {

    private final ServletContext servletContext;
    @Value("${siamois.documents.allowed-types}")
    private String[] mimeTypes;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxUploadSize;

    @Value("${siamois.documents.folder-path}")
    private String documentsPath;

    private final DocumentRepository documentRepository;

    private static int MAX_GENERATIONS = 100;

    public DocumentService(DocumentRepository documentRepository, ServletContext servletContext) {
        this.documentRepository = documentRepository;
        this.servletContext = servletContext;
    }

    @Override
    public List<Document> findWithoutArk(Institution institution) {
        return documentRepository.findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Override
    public ArkEntity save(ArkEntity toSave) {
        return documentRepository.save((Document) toSave);
    }

    public List<MimeType> supportedMimeTypes() {
        List<MimeType> types = new ArrayList<>();
        for (String mimeType : mimeTypes) {
            types.add(MimeType.valueOf(mimeType));
        }
        return types;
    }

    private String generateFileInternalCode() {
        String code;
        int counter = 0;
        do {
            code = CodeUtils.generateCode(Document.FILE_INTERNAL_CODE_LENGTH);
            counter++;
        } while (counter < MAX_GENERATIONS && documentRepository.existsByFileCode(code));

        if (counter == MAX_GENERATIONS)
            throw new IllegalStateException(String.format("Could not generate unique file code after %s generations.", MAX_GENERATIONS));

        return code;
    }

    public Document saveFile(UserInfo userInfo, Document document, InputStream fileInputStream) throws InvalidFileTypeException, InvalidFileSizeException, IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        checkFileData(document);

        document.setMd5Sum(DocumentUtils.md5(bufferedInputStream));
        document.setFileCode(generateFileInternalCode());
        document.setAuthor(userInfo.getUser());
        document.setCreatedByInstitution(userInfo.getInstitution());

        Path folderPath = Paths.get(
                documentsPath,
                userInfo.getInstitution().getId().toString(),
                userInfo.getUser().getId().toString()
        );

        Files.createDirectories(folderPath);

        File serverFile = folderPath.resolve(document.storedFileName()).toFile();
        if (serverFile.createNewFile())
            log.trace("Created new file {}", serverFile.getAbsolutePath());

        try (FileOutputStream outputStream = new FileOutputStream(serverFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        document.setUrl(String.format("%s/content/%s", servletContext.getContextPath(), document.storedFileName()));

        return documentRepository.save(document);
    }

    private void checkFileData(Document document) throws InvalidFileTypeException, InvalidFileSizeException {
        if (Arrays.stream(mimeTypes).noneMatch(type -> type.equals(document.getMimeType()))) {
            throw new InvalidFileTypeException(String.format("Type %s is not allowed", document.getMimeType()));
        }

        if (document.getFileName().length() > DocumentParent.MAX_FILE_NAME_LENGTH) {
            throw new InvalidFileNameException(document.getFileName(), "File name too long");
        }

        final long maxFileSize = DocumentUtils.byteParser(maxUploadSize);

        if (document.getSize() > maxFileSize) {
            throw new InvalidFileSizeException(document.getSize(), String.format("Max file size is %s bytes", maxFileSize));
        }
    }

}
