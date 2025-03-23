package fr.siamois.domain.models.form.customform;

import fr.siamois.converter.CustomFormLayoutConverter;
import fr.siamois.domain.models.form.customfield.CustomField;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "custom_form")
public class CustomForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id", nullable = false)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "custom_form_field",
            joinColumns = { @JoinColumn(name = "fk_field_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_form_id") }
    )
    private Set<CustomField> fields = new HashSet<>();

    @Column(name = "layout", columnDefinition = "jsonb")
    @Convert(converter = CustomFormLayoutConverter.class)
    private List<CustomFormPanel> layout;


}
