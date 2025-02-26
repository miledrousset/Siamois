package fr.siamois.domain.services;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Document;
import fr.siamois.domain.models.Institution;
import fr.siamois.infrastructure.repositories.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService implements ArkEntityService {

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
}
