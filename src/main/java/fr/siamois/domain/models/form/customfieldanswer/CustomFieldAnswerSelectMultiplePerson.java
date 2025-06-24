package fr.siamois.domain.models.form.customfieldanswer;

import fr.siamois.domain.models.auth.Person;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Data
@Entity
@DiscriminatorValue("SELECT_MULTIPLE_PERSON")
@Table(name = "custom_field_answer")
public class CustomFieldAnswerSelectMultiplePerson extends CustomFieldAnswerSelectPerson {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "custom_field_answer_person_answers",
            joinColumns = {
                    @JoinColumn(name = "fk_field_id", referencedColumnName = "fk_field_id"),
                    @JoinColumn(name = "fk_form_response", referencedColumnName = "fk_form_response")
            },
            inverseJoinColumns = { @JoinColumn(name = "fk_person") }
    )
    private List<Person> value = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldAnswerSelectMultiplePerson that)) return false;
        if (!super.equals(o)) return false; // Ensures any inherited fields are compared

        return Objects.equals(getPk(), that.getPk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPk());
    }



}
