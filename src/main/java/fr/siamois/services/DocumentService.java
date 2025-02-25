package fr.siamois.services;

import fr.siamois.infrastructure.repositories.DocumentRepository;
import fr.siamois.models.ArkEntity;
import fr.siamois.models.Document;
import fr.siamois.models.Institution;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService implements ArkEntityService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public List<? extends ArkEntity> findWithoutArk(Institution institution) {
        return documentRepository.findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Override
    public ArkEntity save(ArkEntity toSave) {
        return documentRepository.save((Document) toSave);
    }
}
