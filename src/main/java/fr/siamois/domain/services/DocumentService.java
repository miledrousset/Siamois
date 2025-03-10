package fr.siamois.domain.services;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Document;
import fr.siamois.domain.models.Institution;
import fr.siamois.infrastructure.repositories.DocumentRepository;
import jakarta.validation.constraints.NotNull;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService implements ArkEntityService {

    @Value("${siamois.documents.allowed-types}")
    private String[] mimeTypes;

    @Value("${siamois.documents.folder-path}")
    private String documentsPath;

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
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

    public void saveFile(@NotNull UploadedFile uploadedFile) throws Exception {
        uploadedFile.write(documentsPath);
    }

}
