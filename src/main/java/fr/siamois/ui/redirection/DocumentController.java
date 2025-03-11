package fr.siamois.ui.redirection;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.infrastructure.repositories.DocumentRepository;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

@Slf4j
@Controller
@SessionScoped
public class DocumentController {


    private final DocumentRepository documentRepository;
    private final SessionSettingsBean sessionSettingsBean;
    private final DocumentService documentService;

    public DocumentController(DocumentRepository documentRepository, SessionSettingsBean sessionSettingsBean, DocumentService documentService) {
        this.documentRepository = documentRepository;
        this.sessionSettingsBean = sessionSettingsBean;
        this.documentService = documentService;
    }

    @GetMapping("/content/{fileCodeName}")
    public ResponseEntity<Resource> download(@PathVariable String fileCodeName) {
        UserInfo userInfo = sessionSettingsBean.getUserInfo();
        String[] parts = fileCodeName.split("\\.");
        if (parts.length != 2) {
            return ResponseEntity.badRequest().build();
        }

        String resourceCode = parts[0];
        Optional<Document> opt = documentRepository.findByFileCode(resourceCode);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Document document = opt.get();
        if (!document.getCreatedByInstitution().getId().equals(userInfo.getInstitution().getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        File file = documentService.findFile(document);

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(document.getMimeType()))
                    .body(resource);

        } catch (FileNotFoundException e) {
            log.error("File not found", e);
            return ResponseEntity.notFound().build();
        }

    }

}
