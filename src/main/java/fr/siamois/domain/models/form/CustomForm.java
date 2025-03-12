package fr.siamois.domain.models.form;

import fr.siamois.domain.models.form.customFormField.CustomFormField;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "form")
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
    @OrderBy("id.position ASC") // This ensures the questions are ordered by position
    private List<CustomFormField> questions = new ArrayList<>();


}
