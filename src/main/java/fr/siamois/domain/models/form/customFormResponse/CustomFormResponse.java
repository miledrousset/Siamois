package fr.siamois.domain.models.form.customFormResponse;
import fr.siamois.domain.models.form.CustomForm;
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
    private Long id;

    @ManyToOne
    @JoinColumn(name="fk_custom_form_id")
    private CustomForm form;

    @OneToMany
    @JoinColumn(name="fk_form_response")
    @MapKey(name="pk.field")
    private Map<Long, CustomFieldAnswer> answers = new HashMap<>();

}
