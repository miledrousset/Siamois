package fr.siamois.domain.models.form.customFormResponse;
import fr.siamois.domain.models.form.customForm.CustomForm;
import fr.siamois.domain.models.form.customField.CustomField;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswer;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
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
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "pk.formResponse"
    )
    @MapKey(name="pk.field")
    private Map<CustomField, CustomFieldAnswer> answers = new HashMap<>();

    @OneToOne(mappedBy = "formResponse")
    private RecordingUnit recordingUnit;

    // Keep entities in sync
    public void addAnswer(CustomFieldAnswer answer) {
        this.answers.put(answer.getPk().getField(), answer);
        answer.getPk().setFormResponse(this);
    }

    public void removeAnswer(CustomFieldAnswer answer) {
        answer.getPk().setFormResponse(null);
        this.answers.remove(answer.getPk().getField());
    }


}
