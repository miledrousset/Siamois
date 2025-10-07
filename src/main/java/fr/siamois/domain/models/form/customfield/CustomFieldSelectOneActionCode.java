package fr.siamois.domain.models.form.customfield;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@DiscriminatorValue("SELECT_ONE_ACTION_CODE")
@Table(name = "custom_field")
public class CustomFieldSelectOneActionCode extends CustomField {
    public static class Builder extends CustomField.Builder<Builder, CustomFieldSelectOneActionCode> {
        public Builder() {
            this.field = new CustomFieldSelectOneActionCode();
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
