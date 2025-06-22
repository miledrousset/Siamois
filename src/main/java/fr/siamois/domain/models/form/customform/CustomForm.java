package fr.siamois.domain.models.form.customform;

import fr.siamois.domain.services.attributeconverter.CustomFormLayoutConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
