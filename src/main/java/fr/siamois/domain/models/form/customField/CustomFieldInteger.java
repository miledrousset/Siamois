package fr.siamois.domain.models.form.customField;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;



@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("INTEGER")
@Table(name = "question")
public class CustomFieldInteger extends CustomField {



}
