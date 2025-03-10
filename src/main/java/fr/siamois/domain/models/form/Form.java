package fr.siamois.domain.models.form;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "form")
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id", nullable = false)
    private Long id;

    @Column(name = "description")
    protected String description;

    @Column(name = "name")
    protected String name;

}
