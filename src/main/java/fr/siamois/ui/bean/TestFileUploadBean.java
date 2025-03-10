package fr.siamois.ui.bean;

import fr.siamois.domain.services.DocumentService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.file.UploadedFile;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@SessionScoped
public class TestFileUploadBean {

    private final DocumentService documentService;
    private UploadedFile uploadedFile;

    public TestFileUploadBean(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void upload() {
        if (uploadedFile != null) {
            try {
                documentService.saveFile(uploadedFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public String allowedTypesRegex() {
        List<MimeType> allowedTypes = documentService.supportedMimeTypes();
        List<String> extensions = new ArrayList<>();

        for (MimeType mimeType : allowedTypes) {
            String subtype = mimeType.getSubtype();
            extensions.add(subtype);
        }

        return String.format("/(\\.|\\/)(%s)$/", String.join("|", extensions));
    }

}
