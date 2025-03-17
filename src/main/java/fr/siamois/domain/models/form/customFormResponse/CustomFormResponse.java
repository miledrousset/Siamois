package fr.siamois.domain.models.form.customFormResponse;
import fr.siamois.domain.models.form.CustomForm;
import fr.siamois.domain.models.form.customField.CustomField;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswer;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="fk_form_response")
    @MapKey(name="pk.field")
    private Map<CustomField, CustomFieldAnswer> answers = new HashMap<>();

}
