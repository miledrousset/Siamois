package fr.siamois.ui.bean.dialog;

import fr.siamois.domain.models.vocabulary.Concept;
import org.primefaces.model.file.UploadedFile;

import java.util.List;

public interface DocumentCreationDialog {
    String getDocTitle();
    void setDocTitle(String title);

    Concept getDocNature();
    void setDocNature(Concept nature);

    Concept getDocScale();
    void setDocScale(Concept scale);

    Concept getDocType();
    void setDocType(Concept documentType);

    UploadedFile getDocFile();
    void setDocFile(UploadedFile file);

    void createDocument();

    String getUrlForConcept(Concept concept);

    Concept getParentNature();
    Concept getParentScale();
    Concept getParentType();

    List<Concept> autocompleteNature(String input);
    List<Concept> autocompleteScale (String input);
    List<Concept> autocompleteType (String input);
}
