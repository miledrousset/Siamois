package fr.siamois.domain.models.document;

import fr.siamois.domain.models.ArkEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "siamois_document", schema = "public")
public class Document extends DocumentParent implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id", nullable = false)
    private Long id;

    public String storedFileName() {
        MimeType type = MimeTypeUtils.parseMimeType(getMimeType());
        return fileCode + "." + type.getSubtype();
    }

}