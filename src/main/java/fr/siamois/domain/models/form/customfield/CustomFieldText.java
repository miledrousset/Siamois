package fr.siamois.domain.models.form.customfield;

import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@DiscriminatorValue("TEXT")
@Table(name = "custom_field")
public class CustomFieldText extends CustomField {

    public static class Builder {

        private String label; // label or label code if system field
        private Boolean isSystemField;
        private Concept concept;

        public CustomFieldText.Builder label(String label) {
            this.label = label;
            return this;
        }

        public CustomFieldText.Builder isSystemField(Boolean isSystemField) {
            this.isSystemField = isSystemField;
            return this;
        }

        public CustomFieldText.Builder concept(Concept concept) {
            this.concept = concept;
            return this;
        }
        public CustomFieldText build() {
            CustomFieldText field = new CustomFieldText();
            field.setIsSystemField(this.isSystemField);
            field.setLabel(this.label);
            field.setConcept(this.concept);
            return field;
        }
    }

}
