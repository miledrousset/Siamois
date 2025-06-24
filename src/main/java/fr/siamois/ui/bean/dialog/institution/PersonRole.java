package fr.siamois.ui.bean.dialog.institution;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.vocabulary.Concept;

public record PersonRole(
        Person person,
        Concept role
) {}
