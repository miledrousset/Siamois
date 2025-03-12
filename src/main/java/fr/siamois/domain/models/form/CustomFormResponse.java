package fr.siamois.domain.models.form;

import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswer;
import fr.siamois.domain.models.recordingunit.StratigraphicRelationship;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "custom_form_response")
public class CustomFormResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_form_response_id", nullable = false)
    private Long id;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @ManyToOne
    @JoinColumn(name = "fk_custom_form_id")
    private CustomForm form;

    @OneToMany(mappedBy = "pk.formResponse", fetch = FetchType.EAGER)
    @OrderBy("pk.position ASC") // This ensures the questions are ordered by position
    private Set<CustomFieldAnswer> answers;

}
