package fr.siamois.domain.services;

import fr.siamois.domain.services.DocumentService;
import fr.siamois.infrastructure.repositories.DocumentRepository;
import fr.siamois.domain.models.Document;
import fr.siamois.domain.models.Institution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(documentRepository);
    }

    @Test
    void findWithoutArk() {
        Institution institution = new Institution();
        institution.setId(1L);
        Document document = new Document();

        when(documentRepository.findAllByArkIsNullAndCreatedByInstitution(institution))
                .thenReturn(Collections.singletonList(document));

        List<Document> result = documentService.findWithoutArk(institution);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(documentRepository, times(1))
                .findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Test
    void save() {
        Document document = new Document();

        when(documentRepository.save(document)).thenReturn(document);

        Document result = (Document) documentService.save(document);

        assertNotNull(result);
        assertEquals(document, result);
        verify(documentRepository, times(1)).save(document);
    }
}