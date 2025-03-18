package fr.siamois.domain.models.form.customFormResponse;
import fr.siamois.domain.models.form.customForm.CustomForm;
import fr.siamois.domain.models.form.customField.CustomField;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswer;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@Table(name = "custom_form_response")
public class CustomFormResponse {


    @Id
    @Column(name="custom_form_response_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="fk_custom_form_id")
    private CustomForm form;

    @OneToMany(
            cascade = CascadeType.MERGE,
            orphanRemoval = true
    )
    @JoinColumn(name="fk_form_response")
    @MapKey(name="pk.field")
    private Map<CustomField, CustomFieldAnswer> answers = new HashMap<>();

}
