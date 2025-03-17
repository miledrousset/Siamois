package fr.siamois.domain.models.form;

import fr.siamois.converter.ConceptListConverter;
import fr.siamois.converter.CustomFormLayoutConverter;
import fr.siamois.domain.models.form.customField.CustomField;
import fr.siamois.domain.models.form.customFormField.CustomFormField;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "id.form", fetch = FetchType.EAGER)
    private Set<CustomFormField> fields = new HashSet<>();

    @Column(name = "layout", columnDefinition = "jsonb")
    @Convert(converter = CustomFormLayoutConverter.class)
    private List<CustomFormPanel> layout;


}
