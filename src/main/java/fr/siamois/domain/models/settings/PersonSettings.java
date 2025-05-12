package fr.siamois.domain.models.settings;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "person_settings")
public class PersonSettings implements Serializable {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_person_id", nullable = false)
    @MapsId
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_default_institution_id")
    private Institution defaultInstitution;

    @Column(name = "default_lang", length = LANG_CODE_LENGTH)
    private String langCode;

    // Expect language codes with ISO 639 format
    public static final int LANG_CODE_LENGTH = 3;

    public PersonSettings() {
        id = null;
        person = null;
        defaultInstitution = null;
        langCode = null;
    }

}
