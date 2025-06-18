package fr.siamois.domain.services.document;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.document.DocumentParent;
import fr.siamois.domain.models.exceptions.InvalidFileSizeException;
import fr.siamois.domain.models.exceptions.InvalidFileTypeException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.ArkEntityService;
import fr.siamois.domain.services.document.compressor.FileCompressor;
import fr.siamois.infrastructure.database.repositories.DocumentRepository;
import fr.siamois.infrastructure.files.DocumentStorage;
import fr.siamois.utils.CodeUtils;
import fr.siamois.utils.DocumentUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Setter
public class DocumentService implements ArkEntityService {


    private final DocumentRepository documentRepository;

    private static final int MAX_GENERATIONS = 100;
    private final DocumentStorage documentStorage;
    private final Collection<FileCompressor> fileCompressors;

    public DocumentService(DocumentRepository documentRepository, DocumentStorage documentStorage, Collection<FileCompressor> fileCompressors) {
        this.documentRepository = documentRepository;
        this.documentStorage = documentStorage;
        this.fileCompressors = fileCompressors;
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
        log.trace("Started to upload document {} to {}", document.getFileName(), userInfo.getInstitution().getId());

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
            checkFileData(document);

            document.setMd5Sum(DocumentUtils.md5(bufferedInputStream));
            document.setFileCode(generateFileInternalCode());
            document.setAuthor(userInfo.getUser());
            document.setCreatedByInstitution(userInfo.getInstitution());
            document.setUrl(String.format("%s/content/%s", contextPath, document.contentFileName()));

            documentStorage.save(userInfo, document, bufferedInputStream);
        }

        log.trace("Finished upload document {} to {}", document.getFileName(), userInfo.getInstitution().getId());

        return documentRepository.save(document);
    }

    void checkFileData(Document document) throws InvalidFileTypeException, InvalidFileSizeException {
        List<MimeType> supportedMimeTypes = supportedMimeTypes();
        if (allWildCardIsNotInMimetypes(supportedMimeTypes) && documentMimeTypeIsNotSupported(document)) {
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

    private boolean documentMimeTypeIsNotSupported(Document document) {
        return supportedMimeTypes().stream().noneMatch(type -> type.toString().equals(document.getMimeType()));
    }

    private static boolean allWildCardIsNotInMimetypes(List<MimeType> supportedMimeTypes) {
        return supportedMimeTypes.stream().noneMatch(type -> type.toString().equals("*/*"));
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

    public List<Document> findForActionUnit(ActionUnit actionUnit) {
        return documentRepository.findDocumentsByActionUnit(actionUnit.getId());
    }

    public List<Document> findForRecordingUnit(RecordingUnit recordingUnit) {
        return documentRepository.findDocumentsByRecordingUnit(recordingUnit.getId());
    }

    public void addToSpatialUnit(Document document, SpatialUnit spatialUnit) {
        documentRepository.addDocumentToSpatialUnit(document.getId(), spatialUnit.getId());
    }

    public Optional<InputStream> findInputStreamOfDocument(Document document) {
        Optional<byte[]> result  = documentStorage.findStreamOf(document);
        if (result.isEmpty())
            return Optional.empty();

        ByteArrayInputStream bais = new ByteArrayInputStream(result.get());

        return Optional.of(bais);
    }

    public long maxFileSize() {
        return DocumentUtils.byteParser(documentStorage.getMaxUploadSize());
    }

    public FileCompressor findCompressorOf(Document document) {
        for (FileCompressor fileCompressor : fileCompressors) {
            if (fileCompressor.isMatchingCompressor(document.mimeTypeObject()))
                return fileCompressor;
        }
        throw new IllegalStateException(String.format("No file compressor found for %s", document.getMimeType()));
    }

    public String getMD5Sum(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        return DocumentUtils.md5(bis);
    }

    public boolean existInSpatialUnitByHash(SpatialUnit spatialUnit, String hash) {
        return documentRepository.existsByHashInSpatialUnit(spatialUnit.getId(), hash);
    }

}
