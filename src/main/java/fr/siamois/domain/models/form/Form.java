package fr.siamois.domain.models.form;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "form")
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id", nullable = false)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "form", fetch = FetchType.EAGER)
    @OrderBy("position ASC") // This ensures the questions are ordered by position
    private List<FormQuestion> questions = new ArrayList<>();

}
