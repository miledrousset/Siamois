package fr.siamois.domain.models;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.services.PanelAttributeConverter;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Entity
@Table(name = "bookmark", schema = "public")
@NoArgsConstructor
public class Bookmark {

    @EmbeddedId
    private BookmarkId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Person person;

    @MapsId("institutionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Institution institution;

    @Convert(converter = PanelAttributeConverter.class)
    @Column(name = "json_panels", length = Integer.MAX_VALUE)
    private List<AbstractPanel> savedPanels;

    public Bookmark(Person person, Institution institution) {
        this.id = new BookmarkId(person, institution);
        this.person = person;
        this.institution = institution;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class BookmarkId {
        private Long userId;
        private Long institutionId;

        public BookmarkId(Person person, Institution institution) {
            this.userId = person.getId();
            this.institutionId = institution.getId();
        }

    }

}
