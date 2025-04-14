package fr.siamois.ui.bean.settings.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionElement {
    private String bootstrapIconClass;
    private String title;
    private String description;
    private RedirectAction actionFromBean;
}
