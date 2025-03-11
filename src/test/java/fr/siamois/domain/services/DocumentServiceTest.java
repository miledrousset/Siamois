package fr.siamois.domain.services;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.infrastructure.repositories.DocumentRepository;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MimeType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private ServletContext servletContext;

    @InjectMocks
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(documentRepository, servletContext);
        String[] mimeTypes = {"application/pdf", "image/jpeg"};
        documentService.setMimeTypes(mimeTypes);
        documentService.setMaxUploadSize("10MB");
        documentService.setDocumentsPath("/tmp/siamois");
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

    @Test
    void supportedMimeTypes() {
        List<MimeType> result = documentService.supportedMimeTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(MimeType.valueOf("application/pdf"), result.get(0));
        assertEquals(MimeType.valueOf("image/jpeg"), result.get(1));
    }

    @Test
    void saveFile() throws Exception {
        UserInfo userInfo = new UserInfo(new Institution(), new Person(), "fr");
        userInfo.getInstitution().setId(1L);
        userInfo.getUser().setId(1L);
        Document document = new Document();
        document.setMimeType("application/pdf"); // Définir le type MIME
        document.setFileName("test.pdf"); // Définir le nom du fichier
        document.setSize(1024L); // Définir la taille du fichier
        InputStream inputStream = new ByteArrayInputStream("test data".getBytes());

        when(documentRepository.save(document)).thenReturn(document);
        when(servletContext.getContextPath()).thenReturn("/context");

        Document result = documentService.saveFile(userInfo, document, inputStream);

        assertNotNull(result);
        assertEquals(document, result);
        verify(documentRepository, times(1)).save(document);
    }

    @Test
    void findFile() {
        Institution institution = new Institution();
        institution.setId(1L); // Définir l'ID de l'institution
        Person author = new Person();
        author.setId(1L); // Définir l'ID de l'auteur
        Document document = new Document();
        document.setCreatedByInstitution(institution);
        document.setAuthor(author);
        document.setMimeType("application/pdf");

        File file = documentService.findFile(document);

        assertNotNull(file);
        assertTrue(file.getPath().contains(document.storedFileName()));
    }

    @Test
    void findByFileCode() {
        Document document = new Document();
        when(documentRepository.findByFileCode("code")).thenReturn(Optional.of(document));

        Optional<Document> result = documentService.findByFileCode("code");

        assertTrue(result.isPresent());
        assertEquals(document, result.get());
    }

    @Test
    void findForSpatialUnit() {
        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setId(1L);
        Document document = new Document();

        when(documentRepository.findDocumentsBySpatialUnit(spatialUnit.getId()))
                .thenReturn(Collections.singletonList(document));

        List<Document> result = documentService.findForSpatialUnit(spatialUnit);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(documentRepository, times(1))
                .findDocumentsBySpatialUnit(spatialUnit.getId());
    }

    @Test
    void addToSpatialUnit() {
        Document document = new Document();
        SpatialUnit spatialUnit = new SpatialUnit();

        documentService.addToSpatialUnit(document, spatialUnit);

        verify(documentRepository, times(1))
                .addDocumentToSpatialUnit(document.getId(), spatialUnit.getId());
    }
}