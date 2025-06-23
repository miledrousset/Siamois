package fr.siamois.domain.models.form.customform;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CustomRow implements Serializable {

    private List<CustomCol> columns;

    public static class Builder {

        private final CustomRow row = new CustomRow();
        private final List<CustomCol> columns = new ArrayList<>();

        public Builder addColumn(CustomCol col) {
            this.columns.add(col);
            return this;
        }

        public Builder addColumns(CustomCol... cols) {
            this.columns.addAll(List.of(cols));
            return this;
        }

        public Builder addColumns(List<CustomCol> cols) {
            this.columns.addAll(cols);
            return this;
        }

        public CustomRow build() {
            row.setColumns(columns);
            return row;
        }
    }

}
