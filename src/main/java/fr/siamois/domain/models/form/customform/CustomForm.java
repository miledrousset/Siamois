package fr.siamois.domain.models.form.customform;

import fr.siamois.converter.CustomFormLayoutConverter;
import fr.siamois.domain.models.form.customfield.CustomField;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "custom_form")
public class CustomForm implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id", nullable = false)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;


    @Column(name = "layout", columnDefinition = "jsonb")
    @Convert(converter = CustomFormLayoutConverter.class)
    private List<CustomFormPanel> layout;


}
