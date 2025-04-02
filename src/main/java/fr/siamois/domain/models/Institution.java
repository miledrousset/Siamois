package fr.siamois.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.settings.InstitutionSettings;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "institution")
public class Institution implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institution_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "institution_name", nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(name = "institution_description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_manager_id", nullable = false)
    private Person manager;

    @NotNull
    @Column(name = "identifier", nullable = false, length = Integer.MAX_VALUE)
    private String identifier;

    @OneToOne(fetch = FetchType.LAZY)
    private InstitutionSettings settings;

    @DefaultValue("NOW()")
    @Column(name = "creation_date", nullable = false)
    @JsonIgnore
    private OffsetDateTime creationDate;

    @JsonProperty("creationDate")
    private String creationDateString() {
        if (creationDate == null) {
            return "";
        }
        return creationDate.atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static final int MAX_NAME_LENGTH = 40;

}