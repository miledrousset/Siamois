package fr.siamois.domain.models.form.customfield;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@Entity
@DiscriminatorValue("SELECT_ONE_PERSON")
@Table(name = "custom_field")
public class CustomFieldSelectOnePerson extends CustomFieldSelectPerson {


}
