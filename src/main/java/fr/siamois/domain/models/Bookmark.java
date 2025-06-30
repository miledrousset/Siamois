package fr.siamois.domain.models;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "bookmark", schema = "public" )
@NoArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id" )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Institution institution;

    @Column(name = "title_code", nullable = false)
    private String titleCode;

    @Column(name = "resource_uri", nullable = false, length = 2000)
    private String resourceUri;

    @Override
    public String toString() {
        return String.format("Bookmark n°%s to %s", id, resourceUri);
    }

    public String getBookmarkColor() {
        if (resourceUri.startsWith("/spatialunit" )) {
            return "var(--context-main-color)";
        } else if (resourceUri.startsWith("/actionunit" )) {
            return "var(--context-main-color)";
        } else if (resourceUri.startsWith("/recordingunit" )) {
            return "var(--ground-main-color)";
        } else if (resourceUri.startsWith("/specimen" )) {
            return "var(--ground-main-color)";
        } else {
            return "var(--siamois-green)";
        }
    }

    public String getBookmarkIcon() {
        if (resourceUri.startsWith("/spatialunit" )) {
            return "bi bi-geo-alt";
        } else if (resourceUri.startsWith("/actionunit" )) {
            return "bi bi-arrow-down-square";
        } else if (resourceUri.startsWith("/recordingunit" )) {
            return "bi bi-pencil-square";
        } else if (resourceUri.startsWith("/specimen" )) {
            return "bi bi-box2";
        } else {
            return "bi bi-bookmark-fill";
        }
    }
}
