package fr.siamois.domain.services.document;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.document.DocumentParent;
import fr.siamois.domain.models.exceptions.InvalidFileSizeException;
import fr.siamois.domain.models.exceptions.InvalidFileTypeException;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.ArkEntityService;
import fr.siamois.domain.utils.CodeUtils;
import fr.siamois.domain.utils.DocumentUtils;
import fr.siamois.infrastructure.files.DocumentStorage;
import fr.siamois.infrastructure.database.repositories.DocumentRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Setter
public class DocumentService implements ArkEntityService {


    private final DocumentRepository documentRepository;

    private static final int MAX_GENERATIONS = 100;
    private final DocumentStorage documentStorage;

    public DocumentService(DocumentRepository documentRepository, DocumentStorage documentStorage) {
        this.documentRepository = documentRepository;
        this.documentStorage = documentStorage;
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
        return documentStorage.supportedMimeTypes();
    }

    private String generateFileInternalCode() {
        String code;
        int counter = 0;
        do {
            code = CodeUtils.generateCode(DocumentParent.FILE_INTERNAL_CODE_LENGTH);
            counter++;
        } while (counter < MAX_GENERATIONS && documentRepository.existsByFileCode(code));

        if (counter == MAX_GENERATIONS)
            throw new IllegalStateException(String.format("Could not generate unique file code after %s generations.", MAX_GENERATIONS));

        return code;
    }

    public Document saveFile(UserInfo userInfo, Document document, InputStream fileInputStream, String contextPath) throws InvalidFileTypeException, InvalidFileSizeException, IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        checkFileData(document);

        document.setMd5Sum(DocumentUtils.md5(bufferedInputStream));
        document.setFileCode(generateFileInternalCode());
        document.setAuthor(userInfo.getUser());
        document.setCreatedByInstitution(userInfo.getInstitution());

        Path filePath = Paths.get(
                contextPath,
                userInfo.getInstitution().getIdentifier(),
                document.storedFileName()
        );

        documentStorage.save(filePath, bufferedInputStream);

        document.setUrl(String.format("%s/content/%s", contextPath, document.storedFileName()));

        return documentRepository.save(document);
    }

    private void checkFileData(Document document) throws InvalidFileTypeException, InvalidFileSizeException {
        if (supportedMimeTypes().stream().noneMatch(type -> type.toString().equals(document.getMimeType()))) {
            throw new InvalidFileTypeException(String.format("Type %s is not allowed", document.getMimeType()));
        }

        if (document.getFileName().length() > DocumentParent.MAX_FILE_NAME_LENGTH) {
            throw new InvalidFileNameException(document.getFileName(), "File name too long");
        }

        final long maxFileSize = DocumentUtils.byteParser(documentStorage.getMaxUploadSize());

        if (document.getSize() > maxFileSize) {
            throw new InvalidFileSizeException(document.getSize(), String.format("Max file size is %s bytes", maxFileSize));
        }
    }

    public Optional<File> findFile(Document document) {
        return documentStorage.find(document);
    }

    public Optional<Document> findByFileCode(String fileCode) {
        return documentRepository.findByFileCode(fileCode);
    }

    public List<Document> findForSpatialUnit(SpatialUnit spatialUnit) {
        return documentRepository.findDocumentsBySpatialUnit(spatialUnit.getId());
    }

    public void addToSpatialUnit(Document document, SpatialUnit spatialUnit) {
        documentRepository.addDocumentToSpatialUnit(document.getId(), spatialUnit.getId());
    }
}
