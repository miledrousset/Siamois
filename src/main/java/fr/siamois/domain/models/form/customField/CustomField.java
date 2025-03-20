package fr.siamois.domain.models.form.customField;

import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customForm.CustomForm;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "custom_field")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "answer_type", discriminatorType = DiscriminatorType.STRING)
public abstract class CustomField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_field_id", nullable = false)
    private Long id;

    @Column(name = "label")
    private String label;

    @Column(name = "hint")
    private String hint;

//    @ManyToMany(mappedBy = "fields")
//    private Set<CustomForm> forms = new HashSet<>();

//    @OneToMany(mappedBy = "pk.field", fetch = FetchType.LAZY)
//    private Set<CustomFieldAnswer> answers = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof CustomField)) return false;

        CustomField field = (CustomField) o;
        // Only compare IDs if both entities are persisted
        if (id == null || field.id == null) {
            return false;
        }
        return id.equals(field.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

}
