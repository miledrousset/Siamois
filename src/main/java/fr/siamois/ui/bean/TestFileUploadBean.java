package fr.siamois.ui.bean;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.exceptions.InvalidFileSizeException;
import fr.siamois.domain.models.exceptions.InvalidFileTypeException;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.utils.DocumentUtils;
import fr.siamois.domain.utils.MessageUtils;
import jakarta.faces.application.FacesMessage;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.file.UploadedFile;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.IOException;

@Component
@Getter
@Setter
@SessionScoped
public class TestFileUploadBean {

    private final DocumentService documentService;
    private final SessionSettingsBean sessionSettingsBean;
    private final LangBean langBean;
    private UploadedFile uploadedFile;

    public TestFileUploadBean(DocumentService documentService, SessionSettingsBean sessionSettingsBean, LangBean langBean) {
        this.documentService = documentService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.langBean = langBean;
    }

    public void upload() {
        UserInfo userInfo = sessionSettingsBean.getUserInfo();
        if (uploadedFile != null) {
            Document document = new Document();
            document.setFileName(uploadedFile.getFileName());
            document.setMimeType(uploadedFile.getContentType());
            document.setSize(uploadedFile.getSize());

            try {
                documentService.saveFile(userInfo, document, uploadedFile.getInputStream());
                MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_INFO, "File uploaded");
            } catch (InvalidFileTypeException e) {
                throw new RuntimeException(e);
            } catch (InvalidFileSizeException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public String allowedTypesRegex() {
        return DocumentUtils.allowedTypesRegex(documentService.supportedMimeTypes());
    }

}
