package fr.siamois.domain.models.form.customfield;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@DiscriminatorValue("SELECT_MULTIPLE_PERSON")
@Table(name = "custom_field")
public class CustomFieldSelectMultiplePerson extends CustomFieldSelectPerson {


}
